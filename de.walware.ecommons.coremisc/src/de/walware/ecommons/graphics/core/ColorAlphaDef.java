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


public class ColorAlphaDef extends ColorRefDef {
	
	
	private float fAlpha;
	
	
	public ColorAlphaDef(final ColorDef ref, final float alpha) {
		super(ref);
		
		if (alpha < 0 || alpha > 1) {
			throw new IllegalArgumentException("alpha"); //$NON-NLS-1$
		}
		setAlpha(alpha);
	}
	
	public ColorAlphaDef(final ColorDef ref, final int alpha255) {
		super(ref);
		
		if (alpha255 < 0 || alpha255 > 255) {
			throw new IllegalArgumentException("alpha"); //$NON-NLS-1$
		}
		setAlpha(((float) alpha255) / 255f);
	}
	
	
	protected void setAlpha(final float alpha) {
		fAlpha = Math.round(alpha * 1000f) / 1000f;
	}
	
	public float getAlpha() {
		return fAlpha;
	}
	
	public int getAlpha255() {
		return Math.round(fAlpha * 255f);
	}
	
	public final void printRGBAHex(final StringBuilder sb) {
		printRGBHex(sb);
		final int alpha255 = getAlpha255();
		if (alpha255 < 0x10) {
			sb.append('0');
		}
		sb.append(Integer.toHexString(alpha255));
	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode() * (int) (235 * (1f + fAlpha));
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof ColorAlphaDef)) {
			return false;
		}
		final ColorAlphaDef other = (ColorAlphaDef) obj;
		return (fAlpha == other.fAlpha && getRef().equals(other.getRef()));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(getRef().toString());
		sb.append(" × \u03B1: "); //$NON-NLS-1$
		sb.append(fAlpha);
		return sb.toString();
	}
	
}
