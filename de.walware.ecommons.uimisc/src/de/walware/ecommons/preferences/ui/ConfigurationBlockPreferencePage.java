/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.ui;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferencePageContainer;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.util.PreferenceUtils;
import de.walware.ecommons.preferences.internal.ui.Messages;
import de.walware.ecommons.ui.IOverlayStatus;
import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.components.StatusInfo;
import de.walware.ecommons.ui.util.LayoutUtil;


/**
 * Abstract preference page which is used to wrap a
 * Configuration Block
 */
public abstract class ConfigurationBlockPreferencePage extends PreferencePage
		implements IWorkbenchPreferencePage {
	
	
	private ConfigurationBlock block;
	
	private Composite blockControl;
	
	private IStatus blockStatus;
	
	
	/**
	 * Creates a new preference page.
	 */
	public ConfigurationBlockPreferencePage() {
		this.blockStatus= new StatusInfo();
	}
	
	
	protected abstract ConfigurationBlock createConfigurationBlock() throws CoreException;
	
	@Override
	public void init(final IWorkbench workbench) {
		try {
			this.block= createConfigurationBlock();
		} catch (final CoreException e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, SharedUIResources.PLUGIN_ID, -1,
					NLS.bind(Messages.ConfigurationPage_error_message, getTitle()), e),
					StatusManager.LOG | StatusManager.SHOW);
		}
	}
	
	@Override
	public void dispose() {
		this.block.dispose();
		
		super.dispose();
	}
	
	
	protected ConfigurationBlock getBlock() {
		return this.block;
	}
	
	
	@Override
	protected Control createContents(final Composite parent) {
		this.blockControl= new Composite(parent, SWT.NONE);
		this.blockControl.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		final GridLayout layout= LayoutUtil.createCompositeGrid(1);
		layout.marginRight= LayoutUtil.defaultHSpacing();
		this.blockControl.setLayout(layout);
		this.block.createContents(this.blockControl, getWorkbenchContainer(), getPreferenceStore());
		
		applyDialogFont(this.blockControl);
		
		final String explTitle= this.block.getTitle();
		if (explTitle != null) {
			setTitle(explTitle);
		}
		
		final String helpContext= getHelpContext();
		if (helpContext != null) {
			PlatformUI.getWorkbench().getHelpSystem().setHelp(this.blockControl, helpContext);
		}
		
		return this.blockControl;
	}
	
	protected Composite getBlockControl() {
		return this.blockControl;
	}
	
	
	/**
	 * Overwrite to enable a help context
	 * 
	 * @return the help context for the page or <code>null</code>
	 */
	protected String getHelpContext() {
		return this.block.getHelpContext();
	}
	
	private IWorkbenchPreferenceContainer getWorkbenchContainer() {
		final IPreferencePageContainer container= getContainer();
		return (container instanceof IWorkbenchPreferenceContainer) ? (IWorkbenchPreferenceContainer) container : null;
	}
	
	@Override
	public boolean performOk() {
		if (this.block != null) {
			final IPreferenceSetService preferenceSetService= PreferenceUtils.getPreferenceSetService();
			final IWorkbenchPreferenceContainer container= getWorkbenchContainer();
			final String sourceId= "Obj" + System.identityHashCode((container != null) ? container : this); //$NON-NLS-1$
			final boolean resume= preferenceSetService.pause(sourceId);
			try {
				if (!this.block.performOk()) {
					return false;
				}
			}
			finally {
				if (resume) {
					if (container != null) {
						container.registerUpdateJob(preferenceSetService.createResumeJob(sourceId));
					}
					else {
						preferenceSetService.resume(sourceId);
					}
				}
			}
		}
		return super.performOk();
	}
	
	@Override
	public void performApply() {
		if (this.block != null) {
			final IPreferenceSetService preferenceSetService= PreferenceUtils.getPreferenceSetService();
			final String sourceId= "Obj" + System.identityHashCode(this); //$NON-NLS-1$
			final boolean resume= preferenceSetService.pause(sourceId);
			try {
				this.block.performApply();
			}
			finally {
				if (resume) {
					preferenceSetService.resume(sourceId);
				}
			}
		}
	}
	
	@Override
	public void performDefaults() {
		if (this.block != null) {
			this.block.performDefaults();
		}
		super.performDefaults();
	}
	
	@Override
	public boolean performCancel() {
		if (this.block != null) {
			this.block.performCancel();
		}
		return true;
	}
	
	
	/**
	 * Returns a new status change listener
	 * @return The new listener
	 */
	protected IStatusChangeListener createStatusChangedListener() {
		return new IStatusChangeListener() {
			@Override
			public void statusChanged(final IStatus status) {
				ConfigurationBlockPreferencePage.this.blockStatus= status;
				updateStatus();
			}
		};
	}
	
	protected void updateStatus() {
		updateStatus(this.blockStatus);
	}
	
	protected void updateStatus(final IStatus status) {
		if (status instanceof IOverlayStatus) {
			setValid(((IOverlayStatus) status).getCombinedSeverity() != IStatus.ERROR);
		}
		else {
			setValid(!status.matches(IStatus.ERROR));
		}
		StatusInfo.applyToStatusLine(this, status);
	}
	
	protected IStatus getBlockStatus() {
		return this.blockStatus;
	}
	
}
