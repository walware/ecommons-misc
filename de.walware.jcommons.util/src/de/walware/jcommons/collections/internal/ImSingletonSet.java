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

package de.walware.jcommons.collections.internal;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;

import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;


/**
 * Immutable set implementation for a single element.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Collections.singletonList(...))</code>.</p>
 * 
 * @since de.walware.ecommons.coremisc 1.5
 */
public final class ImSingletonSet<E> extends AbstractImList<E> implements ImSet<E>,
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
			return ImSingletonSet.this.e;
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
			return ImSingletonSet.this.e;
		}
		
	}
	
	
	private final E e;
	
	
	/**
	 * Create a new constant list with the specified element.
	 * 
	 * @param e the element the list will contain.
	 */
	public ImSingletonSet(final E e) {
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
		return ((this.e != null) ? this.e.equals(o) : (null == o));
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
		return ((this.e != null) ? this.e.equals(o) : (null == o)) ? 0 : -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		return ((this.e != null) ? this.e.equals(o) : (null == o)) ? 0 : -1;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return new Iter(0);
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
	public
	void copyTo(final Object[] dest, final int destPos) {
		dest[destPos]= this.e;
	}
	
	@Override
	public
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		assert (length == 1);
		dest[destPos]= this.e;
	}
	
	@Override
	public
	ImList<E> toImList() {
		return new ImSingletonList<>(this.e);
	}
	
	@Override
	public
	ImIdentityList<E> toImIdentityList() {
		return new ImSingletonIdentityList<>(this.e);
	}
	
	
	@Override
	public int hashCode() {
		return (this.e != null) ? this.e.hashCode() : 0;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Set) {
			final Set<?> other= (Set<?>) obj;
			return (1 == other.size()
					&& contains(other.iterator().next()) );
		}
		return false;
	}
	
	@Override
	public String toString() {
		return '[' + String.valueOf(this.e) + ']';
	}
	
}
