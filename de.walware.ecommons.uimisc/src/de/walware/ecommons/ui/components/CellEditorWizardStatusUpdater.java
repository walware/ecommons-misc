/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.wizard.WizardPage;


public class CellEditorWizardStatusUpdater implements ICellEditorListener {
	
	
	private static final String NO_ERROR = ""; //$NON-NLS-1$
	
	
	private final CellEditor fCellEditor;
	private final WizardPage fPage;
	
	private String fRestore;
	
	
	public CellEditorWizardStatusUpdater(final CellEditor editor, final WizardPage page) {
		fPage = page;
		fCellEditor = editor;
		fCellEditor.addListener(this);
	}
	
	
	@Override
	public void editorValueChanged(final boolean oldValidState, final boolean newValidState) {
		if (fRestore == null) {
			fRestore = fPage.getErrorMessage();
			if (fRestore == null) {
				fRestore = NO_ERROR;
			}
		}
		if (!newValidState) {
			fPage.setErrorMessage(fCellEditor.getErrorMessage());
		}
		else {
			fPage.setErrorMessage(null);
		}
	}
	
	@Override
	public void applyEditorValue() {
		if (fRestore != null) {
			fPage.setErrorMessage((fRestore != NO_ERROR) ? fRestore : null);
			fRestore = null;
		}
	}
	
	@Override
	public void cancelEditor() {
		if (fRestore != null) {
			fPage.setErrorMessage((fRestore != NO_ERROR) ? fRestore : null);
			fRestore = null;
		}
	}
	
}
