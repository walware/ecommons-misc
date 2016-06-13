/*=============================================================================#
 # Copyright (c) 2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.internal.core;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.debug.core.model.IDebugElement;


public class DebugElementPropertyTester extends PropertyTester {
	
	
	public DebugElementPropertyTester() {
	}
	
	
	@Override
	public boolean test(final Object receiver, final String property,
			final Object[] args, final Object expectedValue) {
		final IDebugElement element;
		if (receiver instanceof IDebugElement) {
			element= (IDebugElement) receiver;
		}
		else if (receiver instanceof IAdaptable) {
			element= (IDebugElement) ((IAdaptable) receiver).getAdapter(IDebugElement.class);
		}
		else {
			element= null;
		}
		if (element != null) {
			if (property.equals("equalsModelIdentifier")) { //$NON-NLS-1$
				if (expectedValue instanceof String) {
					return (expectedValue.equals(element.getModelIdentifier()));
				}
				return false;
			}
		}
		return false;
	}
	
}
