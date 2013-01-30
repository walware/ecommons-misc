/*******************************************************************************
 * Copyright (c) 2013 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.graphics.core;


public class HSVColorDef extends ColorDef {
	
	
	protected float fHue;
	protected float fSaturation;
	protected float fValue;
	
	
	public HSVColorDef(final float hue, final float saturation, final float value) {
		if (hue < 0f || hue > 1f) {
			throw new IllegalArgumentException("hue"); //$NON-NLS-1$
		}
		if (saturation < 0f || saturation > 1f) {
			throw new IllegalArgumentException("saturation"); //$NON-NLS-1$
		}
		if (value < 0f || value > 1f) {
			throw new IllegalArgumentException("value"); //$NON-NLS-1$
		}
		
		setHSV(hue, saturation, value);
		
		HSVtoRGB();
	}
	
	public HSVColorDef(final ColorDef def) {
		super(def);
		
		if (def instanceof HSVColorDef) {
			final HSVColorDef other = (HSVColorDef) def;
			fHue = other.fHue;
			fSaturation = other.fSaturation;
			fValue = other.fValue;
		}
		else {
			RGBtoHSV();
		}
	}
	
	
	@Override
	public String getType() {
		return "hsv"; //$NON-NLS-1$
	}
	
	protected void setHSV(final float hue, final float saturation, final float value) {
		fHue = Math.round(hue * 1000f) / 1000f;
		fSaturation = Math.round(saturation * 1000f) / 1000f;
		fValue = Math.round(value * 1000f) / 1000f;
	}
	
	private void HSVtoRGB() {
		float hue = fHue;
		final float saturation = fSaturation;
		final float value = fValue;
		
		float r, g, b;
		if (saturation == 0) {
			r = g = b = value; 
		}
		else {
			if (hue == 1) {
				hue = 0;
			}
			hue *= 6;	
			final int i = (int) hue;
			final float f = hue - i;
			final float p = value * (1 - saturation);
			final float q = value * (1 - saturation * f);
			final float t = value * (1 - saturation * (1 - f));
			switch(i) {
				case 0:
					r = value;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = value;
					b = p;
					break;
				case 2:
					r = p;
					g = value;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = value;
					break;
				case 4:
					r = t;
					g = p;
					b = value;
					break;
				case 5:
				default:
					r = value;
					g = p;
					b = q;
					break;
			}
		}
		fRed = (int) (r * 255 + 0.5);
		fGreen = (int) (g * 255 + 0.5);
		fBlue = (int) (b * 255 + 0.5);	
	}
	
	private void RGBtoHSV() {
		final float r = fRed / 255f;
		final float g = fGreen / 255f;
		final float b = fBlue / 255f;
		final float max = Math.max(Math.max(r, g), b);
		final float min = Math.min(Math.min(r, g), b);
		final float delta = max - min;
		float hue = 0;
		if (delta != 0) {
			if (r == max) {
				hue = (g  - b) / delta;
			}
			else {
				if (g == max) {
					hue = 2 + (b - r) / delta;	
				} else {
					hue = 4 + (r - g) / delta;
				}
			}
			hue /= 6;
			if (hue < 0) {
				hue += 1;
			}
		}
		setHSV(hue, (max == 0) ? 0 : (max - min) / max, max);
	}
	
	
	public float getHue() {
		return fHue;
	}
	
	public float getSaturation() {
		return fSaturation;
	}
	
	public float getValue() {
		return fValue;
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof HSVColorDef)) {
			return false;
		}
		final HSVColorDef other = (HSVColorDef) obj;
		return (fHue == other.fHue && fSaturation == other.fSaturation && fValue == other.fValue);
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("HSV: "); //$NON-NLS-1$
		sb.append(fHue);
		sb.append(", "); //$NON-NLS-1$
		sb.append(fSaturation);
		sb.append(", "); //$NON-NLS-1$
		sb.append(fValue);
		sb.append(" (#"); //$NON-NLS-1$
		printRGBHex(sb);
		sb.append(")"); //$NON-NLS-1$
		return sb.toString();
	}
	
}
