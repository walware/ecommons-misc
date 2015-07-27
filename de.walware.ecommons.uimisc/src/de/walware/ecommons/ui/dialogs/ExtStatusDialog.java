/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.dialogs;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ControlEnableState;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.StatusDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.ui.util.LayoutUtil;


public class ExtStatusDialog extends StatusDialog implements IRunnableContext {
	
	
	protected final static int WITH_RUNNABLE_CONTEXT = 1 << 0;
	protected final static int WITH_DATABINDING_CONTEXT = 1 << 1;
	protected final static int SHOW_INITIAL_STATUS = 1 << 2;
	
	
	public class StatusUpdater implements IStatusChangeListener {
		
		@Override
		public void statusChanged(final IStatus status) {
			updateStatus(status);
		}
		
	}
	
	
	private final int fOptions;
	
	private Composite fProgressComposite;
	private ProgressMonitorPart fProgressMonitorPart;
	private Button fProgressMonitorCancelButton;
	
	private int fActiveRunningOperations;
	
	private Control fProgressLastFocusControl;
	private ControlEnableState fProgressLastContentEnableState;
	private Control[] fProgressLastButtonControls;
	private boolean[] fProgressLastButtonEnableStates;
	
	protected DataBindingSupport fDataBinding;
	
	
	/**
	 * @see StatusDialog#StatusDialog(Shell)
	 */
	public ExtStatusDialog(final Shell parent) {
		this(parent, 0);
	}
	
	/**
	 * @see StatusDialog#StatusDialog(Shell)
	 * 
	 * @param withRunnableContext create elements to provide {@link IRunnableContext}
	 */
	public ExtStatusDialog(final Shell parent, final int options) {
		super(parent);
		fOptions = options;
	}
	
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected Point getInitialSize() {
		final Point savedSize = super.getInitialSize();
		final Point minSize = getShell().computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point(Math.max(savedSize.x, minSize.x), Math.max(savedSize.y, minSize.y));
	}
	
	@Override
	public void create() {
		// E-3.6 Eclipse bug fixed?
		super.create();
		final Button button = getButton(IDialogConstants.OK_ID);
		final Shell shell = getShell();
		if (button != null && shell != null && !shell.isDisposed()) {
			shell.setDefaultButton(button);
		}
		
		if ((fOptions & WITH_DATABINDING_CONTEXT) != 0) {
			initBindings();
		}
	}
	
	protected void initBindings() {
		final DataBindingSupport databinding = new DataBindingSupport(getDialogArea());
		addBindings(databinding);
		databinding.installStatusListener(new StatusUpdater());
		if ((fOptions & SHOW_INITIAL_STATUS) == 0) {
			final IStatus status = getStatus();
			updateStatus(Status.OK_STATUS);
			updateButtonsEnableState(status);
		}
		fDataBinding = databinding;
	}
	
	protected void addBindings(final DataBindingSupport db) {
	}
	
	protected DataBindingSupport getDataBinding() {
		return fDataBinding;
	}
	
	@Override
	protected Control createButtonBar(final Composite parent) {
		final Composite composite = (Composite) super.createButtonBar(parent);
		final GridLayout layout = (GridLayout) composite.getLayout();
		layout.verticalSpacing = 0;
		
		if ((fOptions & WITH_RUNNABLE_CONTEXT) != 0) {
			final Composite monitorComposite = createMonitorComposite(composite);
			final Control[] children = composite.getChildren();
			layout.numColumns = 3;
			((GridData) children[0].getLayoutData()).horizontalSpan++;
			monitorComposite.moveBelow(children[1]);
			monitorComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		}
		
		return composite;
	}
	
	private Composite createMonitorComposite(final Composite parent) {
		fProgressComposite = new Composite(parent, SWT.NULL);
		final GridLayout layout = LayoutUtil.createCompositeGrid(2);
		layout.marginLeft = LayoutUtil.defaultHMargin();
		fProgressComposite.setLayout(layout);
		
		fProgressMonitorPart = new ProgressMonitorPart(fProgressComposite, null);
		fProgressMonitorPart.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		fProgressMonitorCancelButton = createButton(fProgressComposite, 1000, IDialogConstants.CANCEL_LABEL, true);
		
		Dialog.applyDialogFont(fProgressComposite);
		fProgressComposite.setVisible(false);
		return fProgressComposite;
	}
	@Override
	public void run(final boolean fork, final boolean cancelable, final IRunnableWithProgress runnable) throws InvocationTargetException, InterruptedException {
		if ((fOptions & WITH_RUNNABLE_CONTEXT) == 0) {
			throw new UnsupportedOperationException();
		}
		if (getShell() != null && getShell().isVisible()) {
			if (fActiveRunningOperations == 0) {
				// Save control state
				fProgressLastFocusControl = getShell().getDisplay().getFocusControl();
				if (fProgressLastFocusControl != null && fProgressLastFocusControl.getShell() != getShell()) {
					fProgressLastFocusControl = null;
				}
				
				fProgressLastContentEnableState = ControlEnableState.disable(getDialogArea());
				final List<Control> buttons = new ArrayList<Control>();
				for (final Control child : getButton(IDialogConstants.OK_ID).getParent().getChildren()) {
					if (child instanceof Button) {
						buttons.add(child);
					}
				}
				fProgressLastButtonControls = buttons.toArray(new Control[buttons.size()]);
				fProgressLastButtonEnableStates = new boolean[fProgressLastButtonControls.length];
				for (int i = 0; i < fProgressLastButtonControls.length; i++) {
					fProgressLastButtonEnableStates[i] = fProgressLastButtonControls[i].getEnabled();
					fProgressLastButtonControls[i].setEnabled(false);
				}
				
				// Enable monitor
				fProgressMonitorCancelButton.setEnabled(cancelable);
				fProgressMonitorPart.attachToCancelComponent(fProgressMonitorCancelButton);
				fProgressComposite.setVisible(true);
				fProgressMonitorCancelButton.setFocus();
				
			}
			
			fActiveRunningOperations++;
			try {
				ModalContext.run(runnable, fork, fProgressMonitorPart, getShell().getDisplay());
			} 
			finally {
				fActiveRunningOperations--;
				
				if (fActiveRunningOperations == 0 && getShell() != null) {
					fProgressComposite.setVisible(false);
					fProgressLastContentEnableState.restore();
					for (int i = 0; i < fProgressLastButtonControls.length; i++) {
						fProgressLastButtonControls[i].setEnabled(fProgressLastButtonEnableStates[i]);
					}
					
					fProgressMonitorPart.removeFromCancelComponent(fProgressMonitorCancelButton);
					if (fProgressLastFocusControl != null) {
						fProgressLastFocusControl.setFocus();
					}
				}
			}
		} 
		else {
			PlatformUI.getWorkbench().getProgressService().run(fork, cancelable, runnable);
		}
	}
	
	@Override
	protected void updateButtonsEnableState(final IStatus status) {
		super.updateButtonsEnableState(status);
		if (fActiveRunningOperations > 0) {
			final Button okButton = getButton(IDialogConstants.OK_ID);
			for (int i = 0; i < fProgressLastButtonControls.length; i++) {
				if (fProgressLastButtonControls[i] == okButton) {
					fProgressLastButtonEnableStates[i] = okButton.isEnabled();
				}
			}
		}
	}
	
}
