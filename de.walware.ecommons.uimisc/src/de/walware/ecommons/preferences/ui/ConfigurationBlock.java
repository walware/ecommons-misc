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

import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.statushandlers.StatusManager;
import org.osgi.service.prefs.BackingStoreException;

import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.ui.util.LayoutUtil;


public abstract class ConfigurationBlock {
	
	
	public static GridData applyWrapWidth(final GridData gd) {
		gd.widthHint= 300;
		return gd;
	}
	
	public static void scheduleChangeNotification(final IWorkbenchPreferenceContainer container, final String[] groupIds, final boolean directly) {
		if (groupIds != null) {
			final String source= (directly) ? null : container.toString();
			final Job job= PreferencesUtil.getSettingsChangeNotifier().getNotifyJob(source, groupIds);
			if (job == null) {
				return;
			}
			if (directly) {
				job.schedule();
			}
			else {
				container.registerUpdateJob(job);
			}
		}
	}
	
	protected class LinkSelectionListener extends SelectionAdapter {
		
		
		public LinkSelectionListener() {
		}
		
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			org.eclipse.ui.dialogs.PreferencesUtil.createPreferenceDialogOn(getShell(), e.text,
					null, getData(e));
		}
		
		protected Object getData(final SelectionEvent e) {
			return null;
		}
		
	}
	
	
	private Shell shell;
	
	private final String title;
	
	private IWorkbenchPreferenceContainer container;
	
	private boolean useProjectSettings= true;
	
	
	protected ConfigurationBlock() {
		this(null);
	}
	
	protected ConfigurationBlock(final String title) {
		this.title= title;
	}
	
	
	public IWorkbenchPreferenceContainer getContainer() {
		return this.container;
	}
	
	public void createContents(final Composite pageComposite, final IWorkbenchPreferenceContainer container,
			final IPreferenceStore preferenceStore) {
		this.shell= pageComposite.getShell();
		this.container= container;
		createBlockArea(pageComposite);
	}
	
	protected abstract void createBlockArea(Composite pageComposite);
	
	public void dispose() {
	}
	
	protected String getTitle() {
		return this.title;
	}
	
	/**
	 * Returns the help context for the configuration block.
	 * <p>
	 * It is used by configuration block containers to set the help context automatically.</p>
	 * 
	 * @return the help context id or <code>null</code>, if not available
	 */
	protected String getHelpContext() {
		return null;
	}
	
	public void performApply() {
		performOk();
	}
	
	public abstract boolean performOk();
	
	public abstract void performDefaults();
	
	public void performCancel() {
	}
	
	public void setUseProjectSpecificSettings(final boolean enable) {
		this.useProjectSettings= enable;
	}
	
	public boolean isUseProjectSpecificSettings() {
		return this.useProjectSettings;
	}
	
	protected Shell getShell() {
		return this.shell;
	}
	
	protected void addLinkHeader(final Composite pageComposite, final String text) {
		final Link link= addLinkControl(pageComposite, text);
		link.setLayoutData(applyWrapWidth(new GridData(SWT.FILL, SWT.FILL, false, false)));
		LayoutUtil.addSmallFiller(pageComposite, false);
	}
	
	protected Link addLinkControl(final Composite composite, final String text) {
		return addLinkControl(composite, text, new LinkSelectionListener());
	}
	
	protected Link addLinkControl(final Composite composite, final String text,
			final LinkSelectionListener listener) {
		final Link link= new Link(composite, SWT.NONE);
		link.setText(text);
		link.addSelectionListener(listener);
		return link;
	}
	
	protected void scheduleChangeNotification(final Set<String> groupIds, final boolean directly) {
		scheduleChangeNotification(this.container, groupIds.toArray(new String[groupIds.size()]), directly);
	}
	
	protected void logSaveError(final BackingStoreException e) {
		StatusManager.getManager().handle(new Status(IStatus.ERROR,
				"org.osgi.service.prefs", ICommonStatusConstants.INTERNAL_PREF_PERSISTENCE, //$NON-NLS-1$
				"An error occurred when saving preferences to backing store.", e)); //$NON-NLS-1$
	}
	
}
