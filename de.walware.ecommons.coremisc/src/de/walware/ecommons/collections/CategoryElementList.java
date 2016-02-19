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

import de.walware.jcommons.collections.SortedArraySet;
import de.walware.jcommons.collections.SortedListSet;


public class CategoryElementList<C, E> extends SortedArraySet<E> {
	
	
	protected class CategorySubList extends SubList {
		
		
		protected final C category;
		
		
		public CategorySubList(final C category, final int offset, final int size) {
			super(offset, size);
			
			this.category= category;
		}
		
		
		@Override
		protected int superIndexOfE(final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			checkModification();
			if (comparator().compareCategory(this.category,
							((CategoryElementComparator<C, ? super E>) CategoryElementList.this.comparator).getCategory(element) )
					!= 0) {
				return -1;
			}
			return binarySearchCategoryMatch(array(), offset(), offset() + size(), element);
		}
		
		@Override
		protected int superIndexOfEChecked(final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			if (comparator().compareCategory(this.category,
							((CategoryElementComparator<C, ? super E>) CategoryElementList.this.comparator).getCategory(element) )
					!= 0) {
				throw new IllegalArgumentException("Element.category != SubList.category"); //$NON-NLS-1$
			}
			checkModification();
			return binarySearchCategoryMatch(array(), offset(), offset() + size(), element);
		}
		
	}
	
	
	public CategoryElementList(final E[] array, final CategoryElementComparator<C, ? super E> comparator) {
		super(array, comparator);
	}
	
	
	protected final CategoryElementComparator<C, ? super E> comparator() {
		return (CategoryElementComparator<C, ? super E>) this.comparator;
	}
	
	@Override
	public CategoryElementComparator<C, ? super E> getComparator() {
		return (CategoryElementComparator<C, ? super E>) this.comparator;
	}
	
	private int binarySearchCategory(final E[] array, int begin, int end, final C category) {
		end--;
		while (begin <= end) {
			final int mid = (begin + end) >>> 1;
			final int d = comparator().compareCategory(comparator().getCategory(array[mid]), category);
			if (d < 0) {
				begin = mid + 1;
			}
			else if (d > 0) {
				end = mid - 1;
			}
			else {
				return mid;
			}
		}
		return -(begin + 1);
	}
	
	private int binarySearchCategoryMatch(final E[] array, int begin, int end, final E element) {
		end--;
		while (begin <= end) {
			final int mid = (begin + end) >>> 1;
			final int d = comparator().compareElement(array[mid], element);
			if (d < 0) {
				begin = mid + 1;
			}
			else if (d > 0) {
				end = mid - 1;
			}
			else {
				return mid;
			}
		}
		return -(begin + 1);
	}
	
	public SortedListSet<E> subList(final C category) {
		if (category == null) {
			throw new NullPointerException("category"); //$NON-NLS-1$
		}
		
		final E[] array= array();
		final int size= size();
		int start= binarySearchCategory(array, 0, size - 1, category);
		if (start < 0) {
			start= -(start + 1);
			return new CategorySubList(category, start, 0);
		}
		int end= start + 1;
		for (; start > 0; start--) {
			if (comparator().compareCategory(comparator().getCategory(array[start - 1]), category) != 0) {
				break;
			}
		}
		for (; end < size; end++) {
			if (comparator().compareCategory(comparator().getCategory(array[end]), category) != 0) {
				break;
			}
		}
		return new CategorySubList(category, start, end - start);
	}
	
}
