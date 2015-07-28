/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.ui.config;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Text;

import de.walware.ecommons.debug.internal.ui.Messages;
import de.walware.ecommons.ui.components.CustomizableVariableSelectionDialog;
import de.walware.ecommons.ui.components.WidgetToolsButton;
import de.walware.ecommons.ui.util.LayoutUtil;


/**
 * Composite usually used in launch configuration dialogs.
 */
public class InputArgumentsComposite extends Composite {
	
	
	private final String title;
	
	private Text textControl;
	
	
	public InputArgumentsComposite(final Composite parent) {
		this(parent, Messages.InputArguments_label);
	}
	
	public InputArgumentsComposite(final Composite parent, final String title) {
		super(parent, SWT.NONE);
		
		this.title= title;
		createControls();
	}
	
	
	private void createControls() {
		final Composite container= this;
		final GridLayout layout= LayoutUtil.createCompositeGrid(2);
		layout.horizontalSpacing= 0;
		container.setLayout(layout);
		
		final Label label= new Label(container, SWT.LEFT);
		label.setText(this.title);
		label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		
		this.textControl= new Text(container, SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint= LayoutUtil.hintWidth(this.textControl, SWT.DEFAULT);
		gd.heightHint= new PixelConverter(this.textControl).convertHeightInCharsToPixels(4);
		this.textControl.setLayoutData(gd);
		
		final WidgetToolsButton tools= new WidgetToolsButton(this.textControl) {
			@Override
			protected void fillMenu(final Menu menu) {
				InputArgumentsComposite.this.fillMenu(menu);
			}
		};
		tools.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}
	
	protected void fillMenu(final Menu menu) {
		final MenuItem item= new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.InsertVariable_label);
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				final CustomizableVariableSelectionDialog dialog= new CustomizableVariableSelectionDialog(getShell());
				if (dialog.open() != Dialog.OK) {
					return;
				}
				final String variable= dialog.getVariableExpression();
				if (variable == null) {
					return;
				}
				InputArgumentsComposite.this.textControl.insert(variable);
				getTextControl().setFocus();
			}
		});
	}
	
	public Text getTextControl() {
		return this.textControl;
	}
	
	public String getNoteText() {
		return Messages.InputArguments_note;
	}
	
}
