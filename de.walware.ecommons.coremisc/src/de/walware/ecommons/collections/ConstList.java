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

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;


/**
 * Constant list.  The list is unmodifiable by clients and, if not otherwise documented, the client 
 * can assume that the elements of list does not change.
 * <p>
 * Comparable to <code>Collections.unmodifiableList(Array.asList(...))</code>.</p>
 * 
 * @since 1.0
 */
public final class ConstList<E> implements List<E>, RandomAccess {
	
	
	public static <T> ConstList<T> concat(final List<T> l1, final List<T> l2) {
		final Object[] a = new Object[l1.size() + l2.size()];
		System.arraycopy((l1 instanceof ConstList) ? ((ConstList<?>) l1).fArray : l1.toArray(), 0, a, 0, l1.size());
		System.arraycopy((l2 instanceof ConstList) ? ((ConstList<?>) l2).fArray : l2.toArray(), 0, a, l1.size(), l2.size());
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> ConstList<T> concat(final List<T> l1, final List<T> l2, final List<T> l3) {
		final Object[] a = new Object[l1.size() + l2.size() + l3.size()];
		int n;
		System.arraycopy((l1 instanceof ConstList) ? ((ConstList<?>) l1).fArray : l1.toArray(), 0, a, 0, l1.size());
		System.arraycopy((l2 instanceof ConstList) ? ((ConstList<?>) l2).fArray : l2.toArray(), 0, a, n = l1.size(), l2.size());
		System.arraycopy((l3 instanceof ConstList) ? ((ConstList<?>) l3).fArray : l3.toArray(), 0, a, n + l2.size(), l3.size());
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> ConstList<T> concat(final T e1, final List<T> l2) {
		final Object[] a = new Object[1 + l2.size()];
		a[0] = e1;
		System.arraycopy((l2 instanceof ConstList) ? ((ConstList<?>) l2).fArray : l2.toArray(), 0, a, 1, l2.size());
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> ConstList<T> concat(final List<T> l1, final T e2) {
		final Object[] a = new Object[l1.size() + 1];
		System.arraycopy((l1 instanceof ConstList) ? ((ConstList<?>) l1).fArray : l1.toArray(), 0, a, 0, l1.size());
		a[l1.size()] = e2;
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> ConstList<T> concat(final Object[] a1, final List<T> l2) {
		final Object[] a = new Object[a1.length + l2.size()];
		System.arraycopy(a1, 0, a, 0, a1.length);
		System.arraycopy((l2 instanceof ConstList) ? ((ConstList<?>) l2).fArray : l2.toArray(), 0, a, a1.length, l2.size());
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> ConstList<T> concat(final List<T> l1, final Object[] a2) {
		final Object[] a = new Object[l1.size() + a2.length];
		System.arraycopy((l1 instanceof ConstList) ? ((ConstList<?>) l1).fArray : l1.toArray(), 0, a, 0, l1.size());
		System.arraycopy(a2, 0, a, l1.size(), a2.length);
		return new ConstList<T>((T[]) a);
	}
	
	public static <T> void sort(final ConstList<T> list, final Comparator<? super T> comparator) {
		Arrays.sort(list.fArray, comparator);
	}
	
	
	private class Iter implements ListIterator<E> {
		
		
		private int fCursor;
		
		
		Iter(final int index) {
			fCursor = index;
		}
		
		
		@Override
		public void set(final E o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void add(final E o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public boolean hasNext() {
			return (fCursor < fArray.length);
		}
		
		@Override
		public int nextIndex() {
			return fCursor;
		}
		
		@Override
		public E next() {
			if (fCursor >= fArray.length) {
				throw new NoSuchElementException();
			}
			return fArray[fCursor++];
		}
		
		@Override
		public boolean hasPrevious() {
			return (fCursor > 0);
		}
		
		@Override
		public int previousIndex() {
			return fCursor-1;
		}
		
		@Override
		public E previous() {
			if (fCursor <= 0 || fArray.length <= 0) {
				throw new NoSuchElementException();
			}
			return fArray[--fCursor];
		}
		
	}
	
	private class SubList implements List<E>, RandomAccess {
		
		
		private class SubIter implements ListIterator<E> {
			
			
			private int fCursor;
			
			
			SubIter(final int index) {
				fCursor = index;
			}
			
			
			@Override
			public void set(final E o) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void add(final E o) {
				throw new UnsupportedOperationException();
			}
			
			@Override
			public void remove() {
				throw new UnsupportedOperationException();
			}
			
			
			@Override
			public boolean hasNext() {
				return (fCursor < fSize);
			}
			
			@Override
			public int nextIndex() {
				return fCursor;
			}
			
			@Override
			public E next() {
				if (fCursor >= fSize) {
					throw new NoSuchElementException();
				}
				return fArray[fOffset+(fCursor++)];
			}
			
			@Override
			public boolean hasPrevious() {
				return (fCursor > 0);
			}
			
			@Override
			public int previousIndex() {
				return fCursor-1;
			}
			
			@Override
			public E previous() {
				if (fCursor <= 0 || fSize <= 0) {
					throw new NoSuchElementException();
				}
				return fArray[fOffset+(--fCursor)];
			}
			
		}
		
		
		private final int fOffset;
		private final int fSize;
		
		
		public SubList(final int fromIndex, final int toIndex) {
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException("fromIndex(" + fromIndex + ") > toIndex(" + toIndex + ")");
			}
			fOffset = fromIndex;
			fSize = toIndex-fromIndex;
		}
		
		
		@Override
		public E set(final int index, final E element) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean add(final E o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void add(final int index, final E element) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final int index, final Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean remove(final Object o) {
			return false;
		}
		
		@Override
		public E remove(final int index) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean removeAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean retainAll(final Collection<?> c) {
			throw new UnsupportedOperationException();
		}
		
		
		@Override
		public int size() {
			return fSize;
		}
		
		@Override
		public boolean isEmpty() {
			return (fSize == 0);
		}
		
		@Override
		public boolean contains(final Object o) {
			return (indexOf(o) >= 0);
		}
		
		@Override
		public boolean containsAll(final Collection<?> c) {
			final Iterator<?> iter = c.iterator();
			while(iter.hasNext()) {
				if (indexOf(iter.next()) < 0) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public E get(final int index) {
			if (index < 0 || index >= fSize) {
				throw new ArrayIndexOutOfBoundsException();
			}
			return fArray[fOffset+index];
		}
		
		@Override
		public int indexOf(final Object o) {
			final int to = fOffset+fSize;
			if (o == null) {
				for (int i = fOffset; i < to; i++) {
					if (fArray[i] == null) {
						return i-fOffset;
					}
				}
			}
			else {
				for (int i = 0; i < to; i++) {
					if (o.equals(fArray[i])) {
						return i-fOffset;
					}
				}
			}
			return -1;
		}
		
		@Override
		public int lastIndexOf(final Object o) {
			if (o == null) {
				for (int i = fOffset+fSize-1; i >= fOffset; i--) {
					if (fArray[i] == null) {
						return i-fOffset;
					}
				}
			}
			else {
				for (int i = fOffset+fSize-1; i >= fOffset; i--) {
					if (o.equals(fArray[i])) {
						return i-fOffset;
					}
				}
			}
			return -1;
		}
		
		
		@Override
		public Iterator<E> iterator() {
			return new SubIter(0);
		}
		
		@Override
		public ListIterator<E> listIterator() {
			return new SubIter(0);
		}
		
		@Override
		public ListIterator<E> listIterator(final int index) {
			if (index < 0 || index > fSize) {
				throw new IndexOutOfBoundsException();
			}
			return new SubIter(index);
		}
		
		
		@Override
		public List<E> subList(final int fromIndex, final int toIndex) {
			if (fromIndex < 0 || toIndex > fSize) {
				throw new IndexOutOfBoundsException();
			}
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException();
			}
			return new SubList(fOffset+fromIndex, fOffset+toIndex);
		}
		
		@Override
		public Object[] toArray() {
			final Object[] a = new Object[fSize];
			System.arraycopy(fArray, fOffset, a, 0, fSize);
			return a;
		}
		
		@Override
		public <T> T[] toArray(final T[] a) {
			System.arraycopy(fArray, fOffset, a, 0, fSize);
			return a;
		}
		
		
		@Override
		public int hashCode() {
			int hashCode = 1;
			final int to = fOffset+fSize;
			for (int i = fOffset; i < to; i++) {
				hashCode = 31*hashCode + ((fArray[i] == null) ? 0 : fArray[i].hashCode());
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
			final List other = (List) obj;
			if (fSize != other.size()) {
				return false;
			}
			final ListIterator otherIter = other.listIterator();
			final int to = fOffset+fSize;
			for (int i = fOffset; i < to; i++) {
				if (!((fArray[i] == null) ? otherIter.next() == null : fArray[i].equals(otherIter.next()))) {
					return false;
				}
			}
			return true;
		}
		
		@Override
		public String toString() {
			return Arrays.toString(toArray());
		}
		
	}
	
	
	private final E[] fArray;
	
	
	/**
	 * Create a new constant list backed by the given array (directly used!).
	 * 
	 * If the list is published through an API and should be constant, the
	 * the elements of the given array must not any longer be changed.
	 * 
	 * @param a the array by which the list will be backed.
	 */
	public ConstList(final E... a) {
		if (a == null) {
			throw new NullPointerException();
		}
		fArray = a;
	}
	
	/**
	 * Create a new constant list with the elements of the given collection.
	 * 
	 * This creates a constant copy of the given collection.
	 * 
	 * @param c a collection whose elements are to be placed into this list.
	 */
	public ConstList(final Collection<? extends E> c) {
		fArray = (E[]) c.toArray();
	}
	
	
	@Override
	public E set(final int index, final E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean add(final E o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void add(final int index, final E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean remove(final Object o) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public E remove(final int index) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void clear() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public int size() {
		return fArray.length;
	}
	
	@Override
	public boolean isEmpty() {
		return (fArray.length == 0);
	}
	
	@Override
	public boolean contains(final Object o) {
		return (indexOf(o) >= 0);
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		final Iterator<?> e = c.iterator();
		while (e.hasNext()) {
			if(indexOf(e.next()) < 0) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public E get(final int index) {
		return fArray[index];
	}
	
	@Override
	public int indexOf(final Object o) {
		if (o == null) {
			for (int i = 0; i < fArray.length; i++) {
				if (fArray[i] == null) {
					return i;
				}
			}
		}
		else {
			for (int i = 0; i < fArray.length; i++) {
				if (o.equals(fArray[i])) {
					return i;
				}
			}
		}
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		if (o == null) {
			for (int i = fArray.length-1; i >= 0; i--) {
				if (fArray[i] == null) {
					return i;
				}
			}
		}
		else {
			for (int i = fArray.length-1; i >= 0; i--) {
				if (o.equals(fArray[i])) {
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
		if (index < 0 || index > fArray.length) {
			throw new IndexOutOfBoundsException();
		}
		return new Iter(index);
	}
	
	
	@Override
	public List<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || toIndex > fArray.length) {
			throw new IndexOutOfBoundsException();
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException();
		}
		return new SubList(fromIndex, toIndex);
	}
	
	@Override
	public Object[] toArray() {
		final Object[] a = new Object[fArray.length];
		System.arraycopy(fArray, 0, a, 0, fArray.length);
		return a;
	}
	
	@Override
	public <T> T[] toArray(T[] a) {
		if (a.length < fArray.length) {
			a = (T[]) Array.newInstance(a.getClass().getComponentType(), fArray.length);
		}
		System.arraycopy(fArray, 0, a, 0, fArray.length);
		return a;
	}
	
	
	@Override
	public int hashCode() {
		int hashCode = 1;
		for (int i = 0; i < fArray.length; i++) {
			hashCode = 31*hashCode + ((fArray[i] == null) ? 0 : fArray[i].hashCode());
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
		final List other = (List) obj;
		if (fArray.length != other.size()) {
			return false;
		}
		final ListIterator otherIter = other.listIterator();
		for (int i = 0; i < fArray.length; i++) {
			if (!((fArray[i] == null) ? otherIter.next() == null : fArray[i].equals(otherIter.next()))) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return Arrays.toString(fArray);
	}
	
}
