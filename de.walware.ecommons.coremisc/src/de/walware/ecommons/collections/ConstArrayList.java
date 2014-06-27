/*=============================================================================#
 # Copyright (c) 2009-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;


/**
 * Constant list implementation based on an array.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Array.asList(...))</code>.</p>
 * 
 * @since 1.0
 */
@Deprecated
public final class ConstArrayList<E> extends AbstractImList<E> implements ConstList<E>, Set<E>, RandomAccess {
	
	
	@SuppressWarnings({ "rawtypes" })
	static final ConstList EMPTY_CONST_LIST= new ConstArrayList<Object>();
	
	
	private static void copyList(final List<?> src, final Object[] dest, final int destPos, final int length) {
		if (src instanceof AbstractImList<?>) {
			((AbstractImList<?>) src).copyTo(dest, destPos);
		}
		else {
			System.arraycopy(src.toArray(), 0, dest, destPos, length);
		}
	}
	
	public static <T> ConstArrayList<T> concat(final List<T> l1, final List<T> l2) {
		final int n1= l1.size();
		final int n2= l2.size();
		final Object[] a= new Object[n1 + n2];
		copyList(l1, a, 0, n1);
		copyList(l2, a, n1, n2);
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> ConstArrayList<T> concat(final List<T> l1, final List<T> l2, final List<T> l3) {
		final int n1= l1.size();
		final int n2= l2.size();
		final int n3= l3.size();
		final Object[] a= new Object[n1 + n2 + n3];
		copyList(l1, a, 0, n1);
		copyList(l2, a, n1, n2);
		copyList(l3, a, n1 + n2, n3);
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> ConstArrayList<T> concat(final T e1, final List<T> l2) {
		final int n2= l2.size();
		final Object[] a= new Object[1 + n2];
		a[0]= e1;
		copyList(l2, a, 1, n2);
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> ConstArrayList<T> concat(final List<T> l1, final T e2) {
		final int n1= l1.size();
		final Object[] a= new Object[n1 + 1];
		copyList(l1, a, 0, n1);
		a[l1.size()]= e2;
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> ConstArrayList<T> concat(final Object[] a1, final List<T> l2) {
		final int n2= l2.size();
		final Object[] a= new Object[a1.length + n2];
		System.arraycopy(a1, 0, a, 0, a1.length);
		copyList(l2, a, a1.length, n2);
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> ConstArrayList<T> concat(final List<T> l1, final Object[] a2) {
		final int n1= l1.size();
		final Object[] a= new Object[n1 + a2.length];
		copyList(l1, a, 0, n1);
		System.arraycopy(a2, 0, a, n1, a2.length);
		return new ConstArrayList<T>((T[]) a);
	}
	
	public static <T> void sort(final ConstArrayList<T> list, final Comparator<? super T> comparator) {
		Arrays.sort(list.array, comparator);
	}
	
	
	private class Iter extends AbstractImListIter<E> {
		
		
		private int cursor;
		
		
		Iter(final int index) {
			this.cursor= index;
		}
		
		
		@Override
		public boolean hasNext() {
			return (this.cursor < ConstArrayList.this.array.length);
		}
		
		@Override
		public int nextIndex() {
			return this.cursor;
		}
		
		@Override
		public E next() {
			if (this.cursor >= ConstArrayList.this.array.length) {
				throw new NoSuchElementException();
			}
			return ConstArrayList.this.array[this.cursor++];
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
			if (this.cursor <= 0 || ConstArrayList.this.array.length <= 0) {
				throw new NoSuchElementException();
			}
			return ConstArrayList.this.array[--this.cursor];
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
	public ConstArrayList(final E... a) {
		if (a == null) {
			throw new NullPointerException();
		}
		this.array= a;
	}
	
	/**
	 * Create a new constant list with the elements of the given collection.
	 * 
	 * This creates a constant copy of the given collection.
	 * 
	 * @param c a collection whose elements are to be placed into this list.
	 */
	public ConstArrayList(final Collection<? extends E> c) {
		this.array= (E[]) c.toArray();
	}
	
	
	@Override
	public int size() {
		return this.array.length;
	}
	
	@Override
	public boolean isEmpty() {
		return (this.array.length == 0);
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
		}
		else {
			for (int i= 0; i < this.array.length; i++) {
				if (o.equals(this.array[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		if (o == null) {
			for (int i= this.array.length - 1; i >= 0; i--) {
				if (null == this.array[i]) {
					return i;
				}
			}
		}
		else {
			for (int i= this.array.length - 1; i >= 0; i--) {
				if (o.equals(this.array[i])) {
					return i;
				}
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
	public ImList<E> subList(final int fromIndex, final int toIndex) {
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
			return ImEmptyList.INSTANCE;
		}
		else if (l == 1) {
			return new ImSingletonList<E>(this.array[fromIndex]);
		}
		else {
			return new ImArraySubList<E>(this.array, fromIndex, toIndex);
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
	void copyTo(final Object[] dest, final int destPos) {
		System.arraycopy(this.array, 0, dest, destPos, this.array.length);
	}
	
	@Override
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		System.arraycopy(this.array, srcPos, dest, destPos, length);
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
		if (!(obj instanceof List)) {
			return false;
		}
		final List<?> other= (List<?>) obj;
		if (this.array.length != other.size()) {
			return false;
		}
		final Iterator<?> otherIter= other.iterator();
		for (int i= 0; i < this.array.length; i++) {
			if (!((this.array[i] != null) ?
					this.array[i].equals(otherIter.next()) :
					(null == otherIter.next()) )) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(this.array);
	}
	
}
