/*=============================================================================#
 # Copyright (c) 2000-2015 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Control;


public class PixelConverter {
	
	
	// TODO: check if still required
	private final static double FONT_WS_FACTOR;
	static {
		final String ws = Platform.getWS();
		if (ws.equals(Platform.WS_WIN32)) {
			FONT_WS_FACTOR = 0.95;
		}
		else if (ws.equals(Platform.WS_GTK)) {
			FONT_WS_FACTOR = 1.15;
		}
		else {
			FONT_WS_FACTOR = 1.00;
		}
	}
	
	
	private final FontMetrics fFontMetrics;
	private final boolean fMonospace;
	
	
	public PixelConverter(final Control control) {
		final GC gc = new GC(control);
		gc.setFont(control.getFont());
		fFontMetrics= gc.getFontMetrics();
		fMonospace = (gc.stringExtent("i").x == gc.stringExtent("W").x);
		gc.dispose();
	}
	
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertHeightInCharsToPixels(int)
	 */
	public int convertHeightInCharsToPixels(final int chars) {
		return Dialog.convertHeightInCharsToPixels(fFontMetrics, chars);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertHorizontalDLUsToPixels(int)
	 */
	public int convertHorizontalDLUsToPixels(final int dlus) {
		return Dialog.convertHorizontalDLUsToPixels(fFontMetrics, dlus);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertVerticalDLUsToPixels(int)
	 */
	public int convertVerticalDLUsToPixels(final int dlus) {
		return Dialog.convertVerticalDLUsToPixels(fFontMetrics, dlus);
	}
	
	/**
	 * @see org.eclipse.jface.dialogs.DialogPage#convertWidthInCharsToPixels(int)
	 */
	public int convertWidthInCharsToPixels(final int chars) {
		if (fMonospace) {
			return Dialog.convertWidthInCharsToPixels(fFontMetrics, chars);
		}
		else {
			return (int) (FONT_WS_FACTOR * Dialog.convertWidthInCharsToPixels(fFontMetrics, chars));
		}
	}	
	
}
