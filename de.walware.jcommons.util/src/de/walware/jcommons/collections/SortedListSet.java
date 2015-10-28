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

package de.walware.jcommons.collections;

import java.util.Comparator;
import java.util.List;
import java.util.Set;


/**
 * Sorted set, which also implements the list interface.
 */
public interface SortedListSet<E> extends Set<E>, List<E> {
	
	
	/**
	 * Comparator used to compare the elements.
	 * 
	 * If the elements implements {@link Comparable}, the comparator can be unset.
	 * 
	 * @return the comparator or <code>null</code>
	 */
	Comparator<? super E> getComparator();
	
	
	int indexOfE(E element);
	
	/**
	 * 
	 * @param element element to add
	 * @return index of element (>= 0, if element was not yet in the list)
	 */
	int addE(E element);
	
	/**
	 * 
	 * @param startIndex first possible index of element
	 * @param element element to add
	 * @return index of element (>= 0, if element was not yet in the list)
	 */
	int addE(int startIndex, E element);
	
	/**
	 * 
	 * @param element element to remove
	 * @return index of element (>= 0, if element was in the list)
	 */
	int removeE(E element);
	
	@Override
	SortedListSet<E> subList(int fromIndex, int toIndex);
	
	
}
