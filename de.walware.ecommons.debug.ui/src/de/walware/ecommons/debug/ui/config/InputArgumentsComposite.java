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

import java.util.Map;

import org.eclipse.core.variables.IStringVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog.VariableFilter;
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

import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.debug.internal.ui.Messages;
import de.walware.ecommons.ui.components.CustomizableVariableSelectionDialog;
import de.walware.ecommons.ui.components.WidgetToolsButton;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.variables.core.VariableText2;


/**
 * Composite usually used in launch configuration dialogs.
 */
public class InputArgumentsComposite extends Composite {
	
	
	public static final int STYLE_LABEL= 0b0_0000_0000_0001_0000;
	
	public static final int STYLE_SINGLE= SWT.SINGLE;
	public static final int STYLE_MULTI= SWT.MULTI;
	
	
	private static final int DEFAULT_STYLE= (STYLE_LABEL | STYLE_MULTI);
	
	
	private final int style;
	
	private final String label;
	
	private Text textControl;
	
	private VariableText2 variableResolver;
	private ImList<VariableFilter> variableFilters;
	
	
	public InputArgumentsComposite(final Composite parent) {
		this(parent, DEFAULT_STYLE, Messages.InputArguments_label);
	}
	
	public InputArgumentsComposite(final Composite parent, final String label) {
		this(parent, DEFAULT_STYLE, label);
	}
	
	public InputArgumentsComposite(final Composite parent, final int style, final String label) {
		super(parent, SWT.NONE);
		
		this.style= style;
		this.label= label;
		createControls();
	}
	
	
	public void setVariableResolver(final VariableText2 variableResolver) {
		this.variableResolver= variableResolver;
	}
	
	public VariableText2 getVariableResolver() {
		return this.variableResolver;
	}
	
	public void setVariableFilter(final ImList<VariableFilter> variableFilters) {
		this.variableFilters= variableFilters;
	}
	
	public ImList<VariableFilter> getVariableFilters() {
		return this.variableFilters;
	}
	
	
	private void createControls() {
		final Composite container= this;
		final GridLayout layout= LayoutUtil.createCompositeGrid(2);
		layout.horizontalSpacing= 0;
		container.setLayout(layout);
		
		if ((this.style & STYLE_LABEL) != 0) {
			final Label label= new Label(container, SWT.LEFT);
			label.setText(this.label);
			label.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 2, 1));
		}
		
		this.textControl= new Text(container, ((this.style & STYLE_SINGLE) != 0) ?
				(SWT.LEFT | SWT.SINGLE | SWT.BORDER) :
				(SWT.LEFT | SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL) );
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint= LayoutUtil.hintWidth(this.textControl, SWT.DEFAULT);
		if ((this.style & STYLE_LABEL) != 0) {
			gd.heightHint= new PixelConverter(this.textControl).convertHeightInCharsToPixels(4);
		}
		this.textControl.setLayoutData(gd);
		
		final WidgetToolsButton tools= new WidgetToolsButton(this.textControl) {
			@Override
			protected void fillMenu(final Menu menu) {
				InputArgumentsComposite.this.fillToolMenu(menu);
			}
		};
		tools.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
	}
	
	protected void fillToolMenu(final Menu menu) {
		final MenuItem item= new MenuItem(menu, SWT.PUSH);
		item.setText(Messages.InsertVariable_label);
		item.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				handleVariablesButton();
			}
		});
	}
	
	protected void handleVariablesButton() {
		final CustomizableVariableSelectionDialog dialog= new CustomizableVariableSelectionDialog(getShell());
		if (this.variableResolver != null) {
			final Map<String, IStringVariable> extraVariables= this.variableResolver.getExtraVariables();
			if (extraVariables != null) {
				dialog.setAdditionals(extraVariables.values());
			}
		}
		if (this.variableFilters != null) {
			dialog.setFilters(this.variableFilters);
		}
		if (dialog.open() != Dialog.OK) {
			return;
		}
		
		final String variable= dialog.getVariableExpression();
		if (variable == null) {
			return;
		}
		insertText(variable);
	}
	
	protected void insertText(final String text) {
		getTextControl().insert(text);
		getTextControl().setFocus();
	}
	
	
	public Text getTextControl() {
		return this.textControl;
	}
	
	public String getNoteText() {
		return Messages.InputArguments_note;
	}
	
}
