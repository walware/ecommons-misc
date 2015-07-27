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

package de.walware.ecommons.ui.components;

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.FontRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;


/**
 * Provides a tools menu button.
 */
public class WidgetToolsButton extends Composite {
	
	
	static final String FONT_SYMBOLIC_NAME = "de.walware.toolbuttonfont"; //$NON-NLS-1$
	
	static Font getToolButtonFont() {
		final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		if (!fontRegistry.hasValueFor(FONT_SYMBOLIC_NAME)) {
			fontRegistry.addListener(new IPropertyChangeListener() {
				@Override
				public void propertyChange(final PropertyChangeEvent event) {
					if (event.getProperty().equals(JFaceResources.DIALOG_FONT)) {
						updateFont();
					}
				}
			});
			updateFont();
		}
		return fontRegistry.get(FONT_SYMBOLIC_NAME);
	}
	
	private static void updateFont() {
		final FontRegistry fontRegistry = JFaceResources.getFontRegistry();
		final Font dialogFont = fontRegistry.get(JFaceResources.DIALOG_FONT);
		final int size = 1 + Math.max(dialogFont.getFontData()[0].getHeight()*3/5, 6);
		final FontDescriptor descriptor = fontRegistry.getDescriptor(JFaceResources.TEXT_FONT).setHeight(size);
		final Font toolFont = descriptor.createFont(Display.getCurrent());
		fontRegistry.put(FONT_SYMBOLIC_NAME, toolFont.getFontData());
	}
	
	
	private Button fButton;
	private Menu fMenu;
	
	private final Control fTarget;
	
	
	public WidgetToolsButton(final Control target) {
		super(target.getParent(), SWT.NONE);
		setLayout(new FillLayout());
		fTarget = target;
		createButton();
	}
	
	
	@Override
	public void setFont(final Font font) {
		super.setFont(font);
		fButton.setFont(getToolButtonFont());
	}
	
	@Override
	public Point computeSize(final int hint, final int hint2, final boolean changed) {
		final Point computeSize = super.computeSize(hint, hint2, changed);
		
		final int x = Math.max(computeSize.x, 18);
		final int y = Math.min(
				Math.max(computeSize.y, 18),
				fTarget.computeSize(SWT.DEFAULT, SWT.DEFAULT, changed).y );
		return new Point(x, y);
	}
	
	protected void createButton() {
		fButton = new Button(this, (SWT.PUSH | SWT.CENTER));
		updateLabels(false);
		fButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				showMenu();
			}
		});
		fButton.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(final KeyEvent e) {
			}
			@Override
			public void keyReleased(final KeyEvent e) {
				if (e.character == '+') {
					showMenu();
				}
			}
		});
		
		fTarget.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(final FocusEvent e) {
				updateLabels(true);
			}
			@Override
			public void focusLost(final FocusEvent e) {
				updateLabels(false);
			}
		});
		
		fButton.addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(final DisposeEvent e) {
				disposeMenu();
			}
		});
		setFont(null);
	}
	
	public Control getButton() {
		return fButton;
	}
	
	public void showMenu() {
		if (fMenu == null) {
			fMenu = new Menu(fButton);
			fillMenu(fMenu);
		}
		final Rectangle bounds = fButton.getBounds();
		final Point pos = fButton.getParent().toDisplay(bounds.x, bounds.y + bounds.height);
		fMenu.setLocation(pos);
		fMenu.setVisible(true);
	}
	
	public void resetMenu() {
		disposeMenu();
	}
	
	protected void disposeMenu() {
		if (fMenu != null) {
			fMenu.dispose();
			fMenu = null;
		}
	}
	
	protected void updateLabels(final boolean hasFocus) {
		fButton.setText(hasFocus ? "&+" : "+"); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	protected void fillMenu(final Menu menu) {
	}
	
}
