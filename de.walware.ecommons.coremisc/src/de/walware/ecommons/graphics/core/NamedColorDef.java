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


public class NamedColorDef extends ColorDef {
	
	
	private final String fName;
	
	
	public NamedColorDef(final String name, final int red, final int green, final int blue) {
		super(red, green, blue);
		fName = name;
	}
	
	
	public String getName() {
		return fName;
	}
	
	
	@Override
	public String getType() {
		return "rgb-"; //$NON-NLS-1$
	}
	
	@Override
	public int hashCode() {
		return fName.hashCode() + super.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NamedColorDef)) {
			return false;
		}
		final NamedColorDef other = (NamedColorDef) obj;
		return (fName.equals(other.fName) && equalsRGB(other));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(18);
		sb.append(fName);
		sb.append(" (#"); //$NON-NLS-1$
		printRGBHex(sb);
		sb.append(')');
		return sb.toString();
	}
	
}