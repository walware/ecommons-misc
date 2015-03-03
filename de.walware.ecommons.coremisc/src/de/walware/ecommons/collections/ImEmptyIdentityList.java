/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


/**
 * Empty list implementation.
 * <p>
 * Comparable to <code>Collections.emptyList()</code>.</p>
 * 
 * @since 1.5
 */
final class ImEmptyIdentityList<E> extends AbstractImList<E> implements ImIdentityList<E>,
		RandomAccess {
	
	
	@SuppressWarnings("rawtypes")
	static final ImEmptyIdentityList INSTANCE= new ImEmptyIdentityList();
	
	
	private final static Object[] ARRAY= new Object[0];
	
	private final ListIterator<E> iterator= new AbstractImListIter<E>() {
		
		@Override
		public boolean hasNext() {
			return false;
		}
		
		@Override
		public int nextIndex() {
			return 0;
		}
		
		@Override
		public E next() {
			throw new NoSuchElementException();
		}
		
		@Override
		public boolean hasPrevious() {
			return false;
		}
		
		@Override
		public int previousIndex() {
			return -1;
		}
		
		@Override
		public E previous() {
			throw new NoSuchElementException();
		}
		
	};
	
	
	public ImEmptyIdentityList() {
	}
	
	
	@Override
	public int size() {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public boolean contains(final Object o) {
		return false;
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		return c.isEmpty();
	}
	
	@Override
	public E get(final int index) {
		throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
	}
	
	@Override
	public int indexOf(final Object o) {
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		return -1;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return this.iterator;
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return this.iterator;
	}
	
	@Override
	public ListIterator<E> listIterator(final int index) {
		if (index != 0) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return this.iterator;
	}
	
	
	@Override
	public ImIdentityList<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > 0) {
			throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex + ", size= " + 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return this;
	}
	
	@Override
	public Object[] toArray() {
		return ARRAY;
	}
	
	@Override
	public <T> T[] toArray(final T[] dest) {
		if (dest.length > 0) {
			dest[0]= null;
		}
		return dest;
	}
	
	@Override
	void copyTo(final Object[] dest, final int destPos) {
	}
	
	@Override
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		assert (false);
	}
	
	@Override
	ImList<E> toImList() {
		return ImEmptyList.INSTANCE;
	}
	
	@Override
	ImIdentityList<E> toImIdentityList() {
		return this;
	}
	
	
	@Override
	public int hashCode() {
		return 1;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof IdentityList) {
			final List<?> other= (List<?>) obj;
			return (other.isEmpty());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[]"; //$NON-NLS-1$
	}
	
}
