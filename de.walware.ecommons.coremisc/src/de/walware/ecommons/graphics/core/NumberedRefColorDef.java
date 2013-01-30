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


public class NumberedRefColorDef extends ColorRefDef {
	
	
	private final int fNumber;
	
	
	public NumberedRefColorDef(final int number, final ColorDef ref) {
		super(ref);
		fNumber = number;
	}
	
	
	public int getNumber() {
		return fNumber;
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof NumberedRefColorDef)) {
			return false;
		}
		final NumberedRefColorDef other = (NumberedRefColorDef) obj;
		return (fNumber == other.fNumber && getRef().equals(other.getRef()));
	}
	
	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("Id: "); //$NON-NLS-1$
		sb.append(fNumber);
		sb.append(" / "); //$NON-NLS-1$
		sb.append(getRef().toString());
		return sb.toString();
	}
	
}
