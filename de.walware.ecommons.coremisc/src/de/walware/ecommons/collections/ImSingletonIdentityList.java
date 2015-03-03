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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


/**
 * Immutable list implementation for a single element.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Collections.singletonList(...))</code>.</p>
 * 
 * @since 1.5
 */
final class ImSingletonIdentityList<E> extends AbstractImList<E> implements ImIdentityList<E>,
		RandomAccess {
	
	
	private class Iter extends AbstractImListIter<E> {
		
		
		private int cursor;
		
		
		Iter(final int index) {
			this.cursor= index;
		}
		
		
		@Override
		public boolean hasNext() {
			return (this.cursor < 1);
		}
		
		@Override
		public int nextIndex() {
			return this.cursor;
		}
		
		@Override
		public E next() {
			if (this.cursor >= 1) {
				throw new NoSuchElementException();
			}
			this.cursor++;
			return ImSingletonIdentityList.this.e;
		}
		
		@Override
		public boolean hasPrevious() {
			return (this.cursor > 0);
		}
		
		@Override
		public int previousIndex() {
			return this.cursor-1;
		}
		
		@Override
		public E previous() {
			if (this.cursor <= 0) {
				throw new NoSuchElementException();
			}
			this.cursor--;
			return ImSingletonIdentityList.this.e;
		}
		
	}
	
	
	private final E e;
	
	
	/**
	 * Create a new constant list with the specified element.
	 * 
	 * @param e the element the list will contain.
	 */
	public ImSingletonIdentityList(final E e) {
		this.e= e;
	}
	
	
	@Override
	public int size() {
		return 1;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean contains(final Object o) {
		return (this.e == o);
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		final Iterator<?> e= c.iterator();
		while (e.hasNext()) {
			if (!contains(e.next())) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public E get(final int index) {
		if (index != 0) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return this.e;
	}
	
	@Override
	public int indexOf(final Object o) {
		return (this.e == o) ? 0 : -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		return (this.e == o) ? 0 : -1;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return new Iter(0);
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return new Iter(0);
	}
	
	@Override
	public ListIterator<E> listIterator(final int index) {
		if (index < 0 || index > 1) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return new Iter(index);
	}
	
	
	@Override
	public ImIdentityList<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > 1) {
			throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex + ", size= " + 1); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (toIndex - fromIndex == 1) {
			return this;
		}
		else {
			return ImEmptyIdentityList.INSTANCE;
		}
	}
	
	@Override
	public Object[] toArray() {
		return new Object[] { this.e };
	}
	
	@Override
	public <T> T[] toArray(T[] dest) {
		if (dest.length < 1) {
			dest= (T[]) Array.newInstance(((Class<T[]>) dest.getClass()).getComponentType(), 1);
		}
		dest[0]= (T) this.e;
		if (dest.length > 1) {
			dest[1]= null;
		}
		return dest;
	}
	
	@Override
	void copyTo(final Object[] dest, final int destPos) {
		dest[destPos]= this.e;
	}
	
	@Override
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		assert (length == 1);
		dest[destPos]= this.e;
	}
	
	@Override
	ImList<E> toImList() {
		return new ImSingletonList<>(this.e);
	}
	
	@Override
	ImIdentityList<E> toImIdentityList() {
		return this;
	}
	
	
	@Override
	public int hashCode() {
		int hashCode= 1;
		hashCode= 31 * hashCode + ((this.e != null) ? this.e.hashCode() : 0);
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof IdentityList) {
			final List<?> other= (List<?>) obj;
			return (1 == other.size()
					&& ((this.e == other.get(0))) );
		}
		return false;
	}
	
	@Override
	public String toString() {
		return '[' + String.valueOf(this.e) + ']';
	}
	
}
