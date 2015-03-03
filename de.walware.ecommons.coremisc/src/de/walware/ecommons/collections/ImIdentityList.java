/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;


/**
 * Immutable list using object identity for comparisons.
 */
public interface ImIdentityList<E> extends ImList<E>, IdentityList<E> {
	
	
	@Override
	public ImIdentityList<E> subList(int fromIndex, int toIndex);
	
}
