/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.util.Comparator;


public abstract class CategoryElementComparator<C, E> implements Comparator<E> {
	
	
	protected CategoryElementComparator() {
	}
	
	
	@Override
	public final int compare(final E o1, final E o2) {
		final int d= compareCategory(getCategory(o1), getCategory(o2));
		if (d != 0) {
			return d;
		}
		return compareElement(o1, o2);
	}
	
	public abstract C getCategory(E element);
	
	public abstract int compareCategory(C category1, C category2);
	
	public abstract int compareElement(E element1, E element2);
	
}
