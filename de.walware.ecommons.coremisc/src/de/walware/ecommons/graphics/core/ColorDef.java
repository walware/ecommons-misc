/*=============================================================================#
 # Copyright (c) 2013-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.graphics.core;


public class ColorDef {
	
	
	public static final ColorDef parseRGBHex(final String s) {
		try {
			return new ColorDef(
					Integer.parseInt(s.substring(0, 2), 16),
					Integer.parseInt(s.substring(2, 4), 16),
					Integer.parseInt(s.substring(4, 6), 16) );
		}
		catch (final NumberFormatException e) {
			return null;
		}
	}
	
	
	protected int fRed;
	protected int fGreen;
	protected int fBlue;
	
	
	protected ColorDef() {
	}
	
	public ColorDef(final int red, final int green, final int blue) {
		if (red < 0 || red > 255) {
			throw new IllegalArgumentException("red"); //$NON-NLS-1$
		}
		if (green < 0 || green > 255) {
			throw new IllegalArgumentException("green"); //$NON-NLS-1$
		}
		if (blue < 0 || blue > 255) {
			throw new IllegalArgumentException("blue"); //$NON-NLS-1$
		}
		
		fRed = red;
		fGreen = green;
		fBlue = blue;
	}
	
	public ColorDef(final ColorDef def) {
		fRed = def.fRed;
		fGreen = def.fGreen;
		fBlue = def.fBlue;
	}
	
	
	public String getType() {
		return "rgb"; //$NON-NLS-1$
	}
	
	
	public final int getRed() {
		return fRed;
	}
	
	public final int getGreen() {
		return fGreen;
	}
	
	public final int getBlue() {
		return fBlue;
	}
	
	
	@Override
	public int hashCode() {
		return (fRed << 16 | fGreen << 8 | fBlue);
	}
	
	@Override
	public boolean equals(final Object obj) {
		return (obj instanceof ColorDef && equalsRGB((ColorDef) obj));
	}
	
	public final boolean equalsRGB(final ColorDef other) {
		return (other != null
				&& fRed == other.fRed && fGreen == other.fGreen && fBlue == other.fBlue);
	}
	
	public final void printRGBHex(final StringBuilder sb) {
		if (fRed < 0x10) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(fRed));
		if (fGreen < 0x10) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(fGreen));
		if (fBlue < 0x10) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(fBlue));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(7);
		sb.append('#');
		printRGBHex(sb);
		return sb.toString();
	}
	
}
