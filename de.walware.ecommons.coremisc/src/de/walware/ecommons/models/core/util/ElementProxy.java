/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.models.core.util;

import org.eclipse.core.runtime.IAdaptable;


public class ElementProxy implements IElementProxy {
	
	
	private final IAdaptable element;
	
	
	public ElementProxy(final IAdaptable element) {
		if (element == null) {
			throw new NullPointerException("element"); //$NON-NLS-1$
		}
		this.element= element;
	}
	
	
	@Override
	public IAdaptable getElement() {
		return this.element;
	}
	
	
	@Override
	public Object getAdapter(final Class required) {
		return this.element.getAdapter(required);
	}
	
	
	@Override
	public int hashCode() {
		return this.element.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		return (obj instanceof ElementProxy
				&& this.element.equals(((ElementProxy) obj).element) );
	}
	
	
	@Override
	public String toString() {
		return getClass().getCanonicalName() + ": " + this.element.toString(); //$NON-NLS-1$
	}
	
}
