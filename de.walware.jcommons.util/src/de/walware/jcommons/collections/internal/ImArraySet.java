/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.collections.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;

import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;


/**
 * Constant set implementation based on an array.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Array.asList(...))</code>.</p>
 * 
 * @since de.walware.ecommons.coremisc 1.5
 */
public final class ImArraySet<E> extends AbstractImList<E> implements ImSet<E>,
		RandomAccess {
	
	
	private class Iter extends AbstractImListIter<E> {
		
		
		private int cursor;
		
		
		Iter(final int index) {
			this.cursor= index;
		}
		
		
		@Override
		public boolean hasNext() {
			return (this.cursor < ImArraySet.this.array.length);
		}
		
		@Override
		public int nextIndex() {
			return this.cursor;
		}
		
		@Override
		public E next() {
			if (this.cursor >= ImArraySet.this.array.length) {
				throw new NoSuchElementException();
			}
			return ImArraySet.this.array[this.cursor++];
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
			if (this.cursor <= 0 || ImArraySet.this.array.length <= 0) {
				throw new NoSuchElementException();
			}
			return ImArraySet.this.array[--this.cursor];
		}
		
	}
	
	
	private final E[] array;
	
	
	/**
	 * Create a new constant list backed by the given array (directly used!).
	 * 
	 * If the list is published through an API and should be constant, the
	 * the elements of the given array must not any longer be changed.
	 * 
	 * @param a the array by which the list will be backed.
	 */
	public ImArraySet(final E[] a) {
		this.array= a;
	}
	
	
	@Override
	public int size() {
		return this.array.length;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}
	
	@Override
	public boolean contains(final Object o) {
		return (indexOf(o) >= 0);
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		final Iterator<?> e= c.iterator();
		while (e.hasNext()) {
			if (indexOf(e.next()) < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public E get(final int index) {
		return this.array[index];
	}
	
	@Override
	public int indexOf(final Object o) {
		if (o == null) {
			for (int i= 0; i < this.array.length; i++) {
				if (null == this.array[i]) {
					return i;
				}
			}
			return -1;
		}
		else {
			for (int i= 0; i < this.array.length; i++) {
				if (o.equals(this.array[i])) {
					return i;
				}
			}
			return -1;
		}
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		if (o == null) {
			for (int i= this.array.length - 1; i >= 0; i--) {
				if (null == this.array[i]) {
					return i;
				}
			}
			return -1;
		}
		else {
			for (int i= this.array.length - 1; i >= 0; i--) {
				if (o.equals(this.array[i])) {
					return i;
				}
			}
			return -1;
		}
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return new Iter(0);
	}
	
	
	@Override
	public Object[] toArray() {
		final Object[] dest= new Object[this.array.length];
		System.arraycopy(this.array, 0, dest, 0, this.array.length);
		return dest;
	}
	
	@Override
	public <T> T[] toArray(final T[] dest) {
		final int n= this.array.length;
		if (dest.length < n) {
			return Arrays.<T, E>copyOf(this.array, n, (Class<T[]>) dest.getClass());
		}
		System.arraycopy(this.array, 0, dest, 0, n);
		if (dest.length > n) {
			dest[n]= null;
		}
		return dest;
	}
	
	@Override
	public
	void copyTo(final Object[] dest, final int destPos) {
		System.arraycopy(this.array, 0, dest, destPos, this.array.length);
	}
	
	@Override
	public
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		System.arraycopy(this.array, srcPos, dest, destPos, length);
	}
	
	@Override
	public
	ImList<E> toImList() {
		return new ImArrayList<>(this.array);
	}
	
	@Override
	public
	ImIdentityList<E> toImIdentityList() {
		return new ImArrayIdentityList<>(this.array);
	}
	
	
	@Override
	public int hashCode() {
		int hashCode= 0;
		for (int i= 0; i < this.array.length; i++) {
			if (this.array[i] != null) {
				hashCode+= this.array[i].hashCode();
			}
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Set) {
			final Set<?> other= (Set<?>) obj;
			return (this.array.length == other.size()
					&& containsAll(other) );
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(this.array);
	}
	
}
