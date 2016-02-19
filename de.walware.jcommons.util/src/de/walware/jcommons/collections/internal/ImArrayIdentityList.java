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
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import de.walware.jcommons.collections.IdentityList;
import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;


/**
 * Constant list implementation based on an array.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Array.asList(...))</code>.</p>
 * 
 * @since de.walware.ecommons.coremisc 1.5
 */
public final class ImArrayIdentityList<E> extends AbstractImList<E> implements ImIdentityList<E>,
		RandomAccess {
	
	
	private class Iter extends AbstractImListIter<E> {
		
		
		private int cursor;
		
		
		Iter(final int index) {
			this.cursor= index;
		}
		
		
		@Override
		public boolean hasNext() {
			return (this.cursor < ImArrayIdentityList.this.array.length);
		}
		
		@Override
		public int nextIndex() {
			return this.cursor;
		}
		
		@Override
		public E next() {
			if (this.cursor >= ImArrayIdentityList.this.array.length) {
				throw new NoSuchElementException();
			}
			return ImArrayIdentityList.this.array[this.cursor++];
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
			if (this.cursor <= 0 || ImArrayIdentityList.this.array.length <= 0) {
				throw new NoSuchElementException();
			}
			return ImArrayIdentityList.this.array[--this.cursor];
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
	public ImArrayIdentityList(final E[] a) {
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
		for (int i= 0; i < this.array.length; i++) {
			if (o == this.array[i]) {
				return i;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		for (int i= this.array.length - 1; i >= 0; i--) {
			if (o == this.array[i]) {
				return i;
			}
		}
		return -1;
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
		if (index < 0 || index > this.array.length) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return new Iter(index);
	}
	
	
	@Override
	public ImIdentityList<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > this.array.length) {
			throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex + ", size= " + this.array.length); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final int l= toIndex - fromIndex;
		if (l == this.array.length) {
			return this;
		}
		else if (l == 0){
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (l == 1) {
			return new ImSingletonIdentityList<>(this.array[fromIndex]);
		}
		else {
			return new ImArrayIdentitySubList<>(this.array, fromIndex, toIndex);
		}
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
		return this;
	}
	
	
	@Override
	public int hashCode() {
		int hashCode= 1;
		for (int i= 0; i < this.array.length; i++) {
			hashCode= 31 * hashCode + ((this.array[i] != null) ? this.array[i].hashCode() : 0);
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof IdentityList) {
			final List<?> other= (List<?>) obj;
			if (this.array.length != other.size()) {
				return false;
			}
			final Iterator<?> otherIter= other.iterator();
			for (int i= 0; i < this.array.length; i++) {
				if (this.array[i] != otherIter.next()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(this.array);
	}
	
}

final class ImArrayIdentitySubList<E> extends AbstractImList<E> implements ImIdentityList<E>,
		RandomAccess {
	
	
	private class Iter extends AbstractImListIter<E> {
		
		
		private int cursor;
		
		
		Iter(final int index) {
			this.cursor= index;
		}
		
		
		@Override
		public boolean hasNext() {
			return (this.cursor < ImArrayIdentitySubList.this.size);
		}
		
		@Override
		public int nextIndex() {
			return this.cursor;
		}
		
		@Override
		public E next() {
			if (this.cursor >= ImArrayIdentitySubList.this.size) {
				throw new NoSuchElementException();
			}
			return ImArrayIdentitySubList.this.array[ImArrayIdentitySubList.this.offset + (this.cursor++)];
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
			if (this.cursor <= 0 || ImArrayIdentitySubList.this.size <= 0) {
				throw new NoSuchElementException();
			}
			return ImArrayIdentitySubList.this.array[ImArrayIdentitySubList.this.offset + (--this.cursor)];
		}
		
	}
	
	
	private final E[] array;
	private final int offset;
	private final int size;
	
	
	public ImArrayIdentitySubList(final E[] array, final int fromIndex, final int toIndex) {
		this.array= array;
		this.offset= fromIndex;
		this.size= toIndex - fromIndex;
	}
	
	
	@Override
	public int size() {
		return this.size;
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
		final Iterator<?> iter= c.iterator();
		while(iter.hasNext()) {
			if (indexOf(iter.next()) < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public E get(final int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return this.array[this.offset + index];
	}
	
	@Override
	public int indexOf(final Object o) {
		final int toIndex= this.offset + this.size;
		for (int i= this.offset; i < toIndex; i++) {
			if (o == this.array[i]) {
				return i - this.offset;
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		for (int i= this.offset + this.size - 1; i >= this.offset; i--) {
			if (o == this.array[i]) {
				return i - this.offset;
			}
		}
		return -1;
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
		if (index < 0 || index > this.size) {
			throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
		}
		return new Iter(index);
	}
	
	
	@Override
	public ImIdentityList<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > this.size) {
			throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex + ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		final int l= toIndex - fromIndex;
		if (l == this.size) {
			return this;
		}
		else if (l == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (l == 1) {
			return new ImSingletonIdentityList<>(this.array[this.offset + fromIndex]);
		}
		else {
			return new ImArrayIdentitySubList<>(this.array, this.offset + fromIndex, this.offset + toIndex);
		}
	}
	
	@Override
	public Object[] toArray() {
		final Object[] dest= new Object[this.size];
		System.arraycopy(this.array, this.offset, dest, 0, this.size);
		return dest;
	}
	
	@Override
	public <T> T[] toArray(final T[] dest) {
		final int n= this.size;
		if (dest.length < n) {
			return Arrays.<T, E>copyOf(this.array, n, (Class<T[]>) dest.getClass());
		}
		System.arraycopy(this.array, this.offset, dest, 0, n);
		if (dest.length > n) {
			dest[n]= null;
		}
		return dest;
	}
	
	@Override
	public
	void copyTo(final Object[] dest, final int destPos) {
		System.arraycopy(this.array, this.offset, dest, destPos, this.size);
	}
	
	@Override
	public
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		System.arraycopy(this.array, this.offset + srcPos, dest, destPos, length);
	}
	
	@Override
	public
	ImList<E> toImList() {
		return new ImArraySubList<>(this.array, this.offset, this.offset + this.size);
	}
	
	@Override
	public
	ImIdentityList<E> toImIdentityList() {
		return this;
	}
	
	
	@Override
	public int hashCode() {
		int hashCode= 1;
		final int toIndex= this.offset + this.size;
		for (int i= this.offset; i < toIndex; i++) {
			hashCode= 31 * hashCode + ((this.array[i] != null) ? this.array[i].hashCode() : 0);
		}
		return hashCode;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof IdentityList) {
			final List<?> other= (List<?>) obj;
			if (this.size != other.size()) {
				return false;
			}
			final ListIterator<?> otherIter= other.listIterator();
			final int toIndex= this.offset + this.size;
			for (int i= this.offset; i < toIndex; i++) {
				if (this.array[i] != otherIter.next()) {
					return false;
				}
			}
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(toArray());
	}
	
}
