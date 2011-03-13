/*******************************************************************************
 * Copyright (c) 2007-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ui.util;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;


public class LayoutUtil {
	
	private static class DialogValues {
		
		int defaultEntryFieldWidth;
		
		int defaultHMargin;
		int defaultVMargin;
		int defaultHSpacing;
		int defaultVSpacing;
		int defaultIndent;
		int defaultSmallIndent;
		
		public DialogValues() {
			final GC gc = new GC(Display.getCurrent());
			gc.setFont(JFaceResources.getDialogFont());
			final FontMetrics fontMetrics = gc.getFontMetrics();
			
			defaultHMargin = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.HORIZONTAL_MARGIN);
			defaultVMargin = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.VERTICAL_MARGIN);
			defaultHSpacing = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.HORIZONTAL_SPACING);
			defaultVSpacing = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.VERTICAL_SPACING);
			defaultEntryFieldWidth = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.ENTRY_FIELD_WIDTH);
			defaultIndent = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.INDENT);
			defaultSmallIndent = Dialog.convertHorizontalDLUsToPixels(fontMetrics, IDialogConstants.SMALL_INDENT);
			
			gc.dispose();
		}
	}
	
	private static DialogValues gDialogValues;
	
	private static DialogValues getDialogValues() {
		if (gDialogValues == null) {
			JFaceResources.getFontRegistry().addListener(new IPropertyChangeListener() {
				public void propertyChange(final PropertyChangeEvent event) {
					if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
						UIAccess.getDisplay().asyncExec(new Runnable() {
							public void run() {
								gDialogValues = new DialogValues();
							}
						});
					}
				}
			});
			gDialogValues = new DialogValues();
		}
		return gDialogValues;
	}
	
	
	public static int defaultHMargin() {
		return getDialogValues().defaultHMargin;
	}
	
	public static int defaultVMargin() {
		return getDialogValues().defaultVMargin;
	}
	
	public static Point defaultSpacing() {
		return new Point(getDialogValues().defaultHSpacing, getDialogValues().defaultVSpacing);
	}
	
	public static int defaultHSpacing() {
		return getDialogValues().defaultHSpacing;
	}
	
	public static int defaultVSpacing() {
		return getDialogValues().defaultVSpacing;
	}
	
	public static int defaultIndent() {
		return getDialogValues().defaultIndent;
	}
	
	public static int defaultSmallIndent() {
		return getDialogValues().defaultSmallIndent;
	}
	
	public static int hintWidth(final Button button) {
		button.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(button);
		final int widthHint = converter.convertHorizontalDLUsToPixels(IDialogConstants.BUTTON_WIDTH);
		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}
	
	public static GridData hintWidth(final GridData gd, final Button button) {
		gd.widthHint = hintWidth(button);
		return gd;
	}
	
	public static int hintWidth(final Text text, final int numChars) {
		return hintWidth(text, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static int hintWidth(final Text text, final String symbolicName, final int numChars) {
		if (symbolicName != null) {
			text.setFont(JFaceResources.getFontRegistry().get(symbolicName));
		}
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(text);
		final int widthHint = converter.convertWidthInCharsToPixels(numChars);
		return widthHint;
	}
	
	public static int hintWidth(final StyledText text, final String symbolicName, final int numChars) {
		if (symbolicName != null) {
			text.setFont(JFaceResources.getFontRegistry().get(symbolicName));
		}
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(text);
		final int widthHint = converter.convertWidthInCharsToPixels(numChars);
		return widthHint;
	}
	
	public static int hintWidth(final Combo combo, final int numChars) {
		return hintWidth(combo, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static int hintWidth(final Combo combo, final String fontName, final int numChars) {
		combo.setFont(JFaceResources.getFontRegistry().get(fontName));
		if (numChars == -1) {
			return getDialogValues().defaultEntryFieldWidth;
		}
		final PixelConverter converter = new PixelConverter(combo);
		int widthHint = converter.convertWidthInCharsToPixels(numChars+1);
		
		final Rectangle trim = combo.computeTrim(0, 0, 0, 0);
		widthHint += trim.x + trim.width;
		
		if (trim.width == 0 && (combo.getStyle() & SWT.DROP_DOWN) == SWT.DROP_DOWN) {
			final Button button = new Button(combo.getParent(), SWT.ARROW | SWT.DOWN);
			widthHint += button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x + 2;
			button.dispose();
//			widthHint += combo.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
		}
		
		return widthHint;
	}
	
	public static GridData hintWidth(final GridData gd, final Combo combo, final int numChars) {
		return hintWidth(gd, combo, JFaceResources.DIALOG_FONT, numChars);
	}
	
	public static GridData hintWidth(final GridData gd, final Combo combo, final String fontName, final int numChars) {
		gd.widthHint = hintWidth(combo, fontName, numChars);
		return gd;
	}
	
	public static int hintWidth(final Combo combo, final String[] items) {
		int max = 0;
		for (final String s : items) {
			max = Math.max(max, s.length());
		}
		return hintWidth(combo, JFaceResources.DIALOG_FONT, max);
	}
	
	public static int hintWidth(final Table table, final int numChars) {
		table.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(table);
		int heightHint = converter.convertWidthInCharsToPixels(numChars);
		final ScrollBar scrollBar = table.getVerticalBar();
		if (scrollBar != null) {
			heightHint += scrollBar.getSize().x;
		}
		if ((table.getStyle() & SWT.CHECK) == SWT.CHECK) {
			heightHint += 16 + converter.convertHorizontalDLUsToPixels(4) +  converter.convertWidthInCharsToPixels(1);
		}
		return heightHint;
	}
	
	public static int hintWidth(final Table table, final String[] items) {
		int max = 0;
		for (final String s : items) {
			max = Math.max(max, s.length());
		}
		return hintWidth(table, max);
	}
	
	public static int hintWidth(final Table table, final Object[] input, final ILabelProvider labelProvider) {
		int max = 0;
		for (final Object o : input) {
			final String s = labelProvider.getText(o);
			if (s != null) {
				max = Math.max(max, s.length());
			}
		}
		return hintWidth(table, max);
	}
	
	public static int hintHeight(final List control, final int rows) {
		return hintHeightOfStructViewer(control, rows);
	}
	
	public static int hintHeight(final Tree control, final int rows) {
		return hintHeightOfStructViewer(control, rows);
	}
	
	public static int hintHeight(final Table control, final int rows) {
		return hintHeightOfStructViewer(control, rows);
	}
	
	private static int hintHeightOfStructViewer(final Control control, final int rows) {
		control.setFont(JFaceResources.getFontRegistry().get(JFaceResources.DIALOG_FONT));
		final PixelConverter converter = new PixelConverter(control);
		int heightHint = converter.convertHeightInCharsToPixels(rows);
		if ((control.getStyle() & SWT.CHECK) == SWT.CHECK) {
			heightHint += rows * 1;
		}
		return heightHint;
	}
	
	public static int hintHeight(final Label control, final int lines) {
		final PixelConverter converter = new PixelConverter(control);
		return converter.convertHeightInCharsToPixels(lines);
	}
	
	public static int hintHeight(final StyledText control, final int lines) {
		final PixelConverter converter = new PixelConverter(control);
		return converter.convertHeightInCharsToPixels(lines);
	}
	
	
	public static GridLayout applyDialogDefaults(final GridLayout gl, final int numColumns) {
		final DialogValues dialogValues = getDialogValues();
		gl.numColumns = numColumns;
		gl.marginWidth = dialogValues.defaultHMargin;
		gl.marginHeight = dialogValues.defaultVMargin;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout applyCompositeDefaults(final GridLayout gl, final int numColumns) {
		final DialogValues dialogValues = getDialogValues();
		gl.numColumns = numColumns;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout applyGroupDefaults(final GridLayout gl, final int numColumns) {
		final DialogValues dialogValues = getDialogValues();
		gl.numColumns = numColumns;
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout applyContentDefaults(final GridLayout gl, final int numColumns) {
		final DialogValues dialogValues = getDialogValues();
		gl.numColumns = numColumns;
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout applyTabDefaults(final GridLayout gl, final int numColumns) {
		final DialogValues dialogValues = getDialogValues();
		gl.numColumns = numColumns;
		gl.marginWidth = dialogValues.defaultHSpacing;
		gl.marginHeight = dialogValues.defaultVSpacing;
		gl.horizontalSpacing = dialogValues.defaultHSpacing;
		gl.verticalSpacing = dialogValues.defaultVSpacing;
		return gl;
	}
	
	public static GridLayout applySashDefaults(final GridLayout gl, final int numColumns) {
		gl.numColumns = numColumns;
		gl.marginWidth = 0;
		gl.marginHeight = 0;
		gl.horizontalSpacing = 0;
		gl.verticalSpacing = 0;
		return gl;
	}
	
	public static void addGDDummy(final Composite composite) {
		addGDDummy(composite, false);
	}
	public static void addGDDummy(final Composite composite, final boolean grab) {
		final Label dummy = new Label(composite, SWT.NONE);
		dummy.setVisible(false);
		dummy.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, grab, false));
	}
	
	public static void addSmallFiller(final Composite composite, final boolean grab) {
		final Label filler = new Label(composite, SWT.NONE);
		final Layout layout = composite.getLayout();
		if (layout instanceof GridLayout) {
			final GridData gd = new GridData(SWT.FILL, SWT.FILL, false, grab);
			gd.horizontalSpan = ((GridLayout) layout).numColumns;
			gd.heightHint = defaultVSpacing() / 2;
			filler.setLayoutData(gd);
		}
	}
	
	
	private LayoutUtil() {}
	
}
