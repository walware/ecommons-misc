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

package de.walware.ecommons.collections;

import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.RandomAccess;


public class SortedArraySet<E> extends AbstractList<E> implements SortedListSet<E>, RandomAccess {
	
	
	protected class SubList extends AbstractList<E> implements SortedListSet<E>, RandomAccess {
		
		
		private int expectedModCount;
		
		private final int offset;
		private int size;
		
		
		public SubList(final int offset, final int size) {
			this.expectedModCount= SortedArraySet.this.modCount;
			this.offset= offset;
			this.size= size;
		}
		
		
		protected final void checkModification() {
			if (this.expectedModCount != SortedArraySet.this.modCount) {
				throw new ConcurrentModificationException();
			}
		}
		
		protected final void checkSubIndex(final int index) {
			if (index < 0 || index >= this.size) {
				throw new IndexOutOfBoundsException("index= " + index + ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
			}
		}
		
		
		@Override
		public Comparator<? super E> getComparator() {
			return SortedArraySet.this.comparator;
		}
		
		
		protected final int offset() {
			return this.offset;
		}
		
		@Override
		public final int size() {
			checkModification();
			return this.size;
		}
		
		@Override
		public final boolean isEmpty() {
			checkModification();
			return (this.size == 0);
		}
		
		protected int superIndexOfE(final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			checkModification();
			return binarySearch(this.offset, this.offset + this.size, element);
		}
		
		protected int superIndexOfEChecked(final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			checkModification();
			final int toIndex= this.offset + this.size;
			final int index= binarySearch((this.offset > 0) ? (this.offset - 1) : this.offset,
					(toIndex < SortedArraySet.this.size) ? toIndex + 1 : toIndex, element );
			if ((index >= 0) ?
					(index < this.offset || index > toIndex) :
					(-(index + 1) < this.offset || -(index + 1) > toIndex) ) {
				throw new IllegalArgumentException("Outside of subList: e= " + element); //$NON-NLS-1$
			}
			return index;
		}
		
		@Override
		public int indexOfE(final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			checkModification();
			final int index= binarySearch(this.offset, this.offset + this.size, element);
			return (index >= 0) ? index - this.offset : index + this.offset;
		}
		
		@Override
		public int indexOf(final Object o) {
			final int index= indexOfE(castElementObject(o));
			return (index >= 0) ? index : -1;
		}
		
		@Override
		public int lastIndexOf(final Object o) {
			final int index= indexOfE(castElementObject(o));
			return (index >= 0) ? index : -1;
		}
		
		@Override
		public boolean contains(final Object o) {
			final int index= superIndexOfE(castElementObject(o));
			return (index >= 0);
		}
		
		
		protected final int addSuperIndex(final int index, final E element) {
			if (index >= 0) {
				SortedArraySet.this.array[index]= element;
				return -(index - this.offset + 1);
			}
			else {
				this.modCount++;
				addIndex(index, element);
				this.expectedModCount= SortedArraySet.this.modCount;
				this.size++;
				return -(index + this.offset + 1);
			}
		}
		
		@Override
		public int addE(final E element) {
			return addSuperIndex(superIndexOfEChecked(element), element);
		}
		
		@Override
		public int addE(int startIndex, final E element) {
			if (element == null) {
				throw new NullPointerException("element"); //$NON-NLS-1$
			}
			if (startIndex < 0 || startIndex > this.size) {
				throw new IndexOutOfBoundsException("startIndex= " + startIndex+ ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
			}
			checkModification();
			
			startIndex+= this.offset; // to super
			final int toIndex= startIndex + this.size;
			final int index= binarySearch((startIndex > 0) ? (startIndex - 1) : startIndex,
					(toIndex < SortedArraySet.this.size) ? toIndex + 1 : toIndex, element );
			if ((index >= 0) ?
					(index < startIndex) :
					(-(index + 1) < startIndex)) {
				throw new IllegalArgumentException("Element < startIndex: startIndex= " + startIndex); //$NON-NLS-1$
			}
			if ((index >= 0) ?
					(index > toIndex) :
					(-(index + 1) > toIndex)) {
				throw new IllegalArgumentException("Element outside of subList: e= " + element); //$NON-NLS-1$
			}
			return addSuperIndex(index, element);
		}
		
		@Override
		public boolean add(final E element) {
			return (addE(element) >= 0);
		}
		
		@Override
		public void add(final int index, final E element) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean addAll(final Collection<? extends E> c) {
			if (c.isEmpty()) {
				return false;
			}
			boolean modified= false;
			if (c instanceof SortedListSet && SortedArraySet.this.comparator == ((SortedListSet<?>) c).getComparator()) {
				int index= 0;
				for (final Iterator<? extends E> iter= c.iterator(); iter.hasNext();) {
					index= addE(index, iter.next());
					if (index >= 0) {
						modified= true;
						index++;
					}
					else {
						index= -(index + 1);
					}
				}
			}
			else {
				for (final Iterator<? extends E> iter= c.iterator(); iter.hasNext();) {
					if (addE(iter.next()) >= 0) {
						modified= true;
					}
				}
			}
			return modified;
		}
		
		@Override
		public boolean addAll(final int index, final Collection<? extends E> c) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public E set(int index, final E element) {
			checkSubIndex(index);
			index+= this.offset;
			final E previous= SortedArraySet.this.array[index];
			if (compare(previous, element) == 0) {
				this.modCount++;
				SortedArraySet.this.array[index]= element;
				return previous;
			}
			else {
				throw new IllegalArgumentException();
			}
		}
		
		private void removeSuperIndex(final int index) {
			this.modCount++;
			removeIndex(index);
			this.expectedModCount= SortedArraySet.this.modCount;
			this.size--;
		}
		
		@Override
		public int removeE(final E element) {
			final int index= superIndexOfE(element);
			if (index >= 0) {
				removeSuperIndex(index);
				return index - this.offset;
			}
			return index + this.offset;
		}
		
		@Override
		public E remove(int index) {
			checkSubIndex(index);
			checkModification();
			index+= this.offset;
			final E previous= SortedArraySet.this.array[index];
			removeSuperIndex(index);
			return previous;
		}
		
		@Override
		public void clear() {
			checkModification();
			this.modCount++;
			SortedArraySet.this.removeRange(this.offset, this.offset + this.size);
			this.expectedModCount= SortedArraySet.this.modCount;
			this.size= 0;
		}
		
		@Override
		public boolean remove(final Object o) {
			return ((removeE(castElementObject(o)) >= 0));
		}
		
		
		@Override
		public E get(final int index) {
			checkSubIndex(index);
			checkModification();
			return SortedArraySet.this.array[this.offset + index];
		}
		
		@Override
		public Object[] toArray() {
			checkModification();
			final Object[] a= new Object[this.size];
			System.arraycopy(SortedArraySet.this.array, this.offset, a, 0, a.length);
			return a;
		}
		
		@Override
		public <T> T[] toArray(final T[] a) {
			checkModification();
			final int s= this.size;
			if (a.length < s) {
				return Arrays.<T, E>copyOfRange(SortedArraySet.this.array, this.offset, s - this.offset, (Class<T[]>) a.getClass());
			}
			System.arraycopy(SortedArraySet.this.array, this.offset, a, 0, s - this.offset);
			if (a.length > s) {
				a[s]= null;
			}
			return a;
		}
		
		@Override
		public SortedListSet<E> subList(final int fromIndex, final int toIndex) {
			if (fromIndex < 0 || fromIndex > this.size
					|| toIndex < 0 || toIndex > this.size) {
				throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
			}
			if (fromIndex > toIndex) {
				throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return new SubList(this.offset + fromIndex, fromIndex - toIndex);
		}
		
	}
	
	
	protected final Class<E[]> arrayClass;
	
	protected final Comparator<? super E> comparator;
	
	private E[] array;
	
	private int size;
	
	
	/**
	 * Creates a new set.
	 * 
	 * @param array initial presorted array
	 * @param comparator comparator used to sort the elements or <code>null</code>
	 *     to use the natural order of comparable elements
	 */
	public SortedArraySet(final E[] array, final Comparator<? super E> comparator) {
		this.arrayClass= (Class<E[]>) array.getClass();
		if (comparator == null && !Comparable.class.isAssignableFrom(this.arrayClass.getComponentType())) {
			throw new NullPointerException("comparator"); //$NON-NLS-1$
		}
		
		this.comparator= comparator;
		this.array= array;
		this.size= size;
	}
	
	/**
	 * Creates a new set.
	 * 
	 * @param array initial presorted array
	 * @param size initial size
	 * @param comparator comparator used to sort the elements or <code>null</code>
	 *     to use the natural order of comparable elements
	 */
	public SortedArraySet(final E[] array, int size, final Comparator<? super E> comparator) {
		this.arrayClass= (Class<E[]>) array.getClass();
		if (comparator == null && !Comparable.class.isAssignableFrom(this.arrayClass.getComponentType())) {
			throw new NullPointerException("comparator"); //$NON-NLS-1$
		}
		if (size < 0 || size > array.length) {
			throw new IllegalArgumentException("size= " + size); //$NON-NLS-1$
		}
		
		this.comparator= comparator;
		this.array= array;
		this.size= size;
	}
	
	
	@Override
	public Comparator<? super E> getComparator() {
		return this.comparator;
	}
	
	
	protected E castElementObject(final Object o) {
		if (o != null && !this.arrayClass.getComponentType().isAssignableFrom(o.getClass())) {
			throw new ClassCastException(o.getClass().toString());
		}
		return (E) o;
	}
	
	protected final void checkIndex(final int index) {
		if (index < 0 || index >= this.size) {
			throw new IndexOutOfBoundsException("index= " + index + ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	
	public void ensureCapacity(final int minCapacity) {
		if (this.array.length < minCapacity) {
			this.modCount++;
			this.array= Arrays.copyOf(this.array, Math.max((this.array.length * 3)/2 + 1, minCapacity), this.arrayClass);
		}
	}
	
	public void trimToSize() {
		if (this.array.length != this.size) {
			this.modCount++;
			this.array= Arrays.copyOf(this.array, this.size, this.arrayClass);
		}
	}
	
	protected final E[] array() {
		return this.array;
	}
	
	@Override
	public final boolean isEmpty() {
		return (this.size == 0);
	}
	
	@Override
	public final int size() {
		return this.size;
	}
	
	
	protected final int compare(final E element1, final E element2) {
		return (this.comparator != null) ?
				this.compare(element1, element2) :
				((Comparable<? super E>) element1).compareTo(element2);
	}
	
	protected final int binarySearch(final int start, final int end, final E element) {
		return (this.comparator != null) ?
				binarySearchComparator(this.array, start, end, element) :
				binarySearchComparable(this.array, start, end, element);
	}
	
	protected final int binarySearchComparator(final E[] a, int begin, int end, final E element) {
		end--;
		while (begin <= end) {
			final int i = (begin + end) >>> 1;
			final int d = this.comparator.compare(a[i], element);
			if (d < 0) {
				begin = i + 1;
			}
			else if (d > 0) {
				end = i - 1;
			}
			else {
				return i;
			}
		}
		return -(begin + 1);
	}
	
	protected final int binarySearchComparable(final E[] a, int begin, int end, final E element) {
		end--;
		while (begin <= end) {
			final int i = (begin + end) >>> 1;
			final int d = ((Comparable<? super E>) a[i]).compareTo(element);
			if (d < 0) {
				begin = i + 1;
			}
			else if (d > 0) {
				end = i - 1;
			}
			else {
				return i;
			}
		}
		return -(begin + 1);
	}
	
	@Override
	public int indexOfE(final E element) {
		if (element == null) {
			throw new NullPointerException("element"); //$NON-NLS-1$
		}
		return binarySearch(0, this.size, element);
	}
	
	@Override
	public int indexOf(final Object o) {
		final int index= indexOfE(castElementObject(o));
		return (index >= 0) ? index : -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		final int index= indexOfE(castElementObject(o));
		return (index >= 0) ? index : -1;
	}
	
	@Override
	public boolean contains(final Object o) {
		return (indexOfE(castElementObject(o)) >= 0);
	}
	
	
	@Override
	public E get(final int index) {
		checkIndex(index);
		return this.array[index];
	}
	
	
	protected final int addIndex(int index, final E element) {
		if (index >= 0) {
			this.array[index]= element;
			return -(index + 1);
		}
		else {
			this.modCount++;
			ensureCapacity(this.size + 1);
			index= -(index + 1);
			if (index < this.size) {
				System.arraycopy(this.array, index, this.array, index + 1, this.size - index);
			}
			this.array[index]= element;
			this.size++;
			return index;
		}
	}
	
	@Override
	public int addE(final E element) {
		return addIndex(indexOfE(element), element);
	}
	
	@Override
	public int addE(final int startIndex, final E element) {
		if (element == null) {
			throw new NullPointerException("element"); //$NON-NLS-1$
		}
		if (startIndex < 0 || startIndex > this.size) {
			throw new IndexOutOfBoundsException("startIndex= " + startIndex+ ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
		}
		
		final int index= binarySearch((startIndex > 0) ? startIndex - 1 : startIndex,
				this.size, element );
		if ((index >= 0) ?
				(index < startIndex) :
				(-(index + 1) < startIndex) ) {
			throw new IllegalArgumentException("Index of element < startIndex: startIndex= " + startIndex); //$NON-NLS-1$
		}
		return addIndex(index, element);
	}
	
	@Override
	public boolean add(final E element) {
		return (addE(element) >= 0);
	}
	
	@Override
	public void add(final int index, final E element) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean addAll(final Collection<? extends E> c) {
		if (c.isEmpty()) {
			return false;
		}
		boolean modified= false;
		if (c instanceof SortedListSet && this.comparator == ((SortedListSet<?>) c).getComparator()) {
			int index= 0;
			for (final Iterator<? extends E> iter= c.iterator(); iter.hasNext();) {
				index= addE(index, iter.next());
				if (index >= 0) {
					modified= true;
					index++;
				}
				else {
					index= -(index + 1);
				}
			}
		}
		else {
			for (final Iterator<? extends E> iter= c.iterator(); iter.hasNext();) {
				if (addE(iter.next()) >= 0) {
					modified= true;
				}
			}
		}
		return modified;
	}
	
	@Override
	public boolean addAll(final int index, final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Adds the elements of a presorted array to this set.
	 * 
	 * @param index
	 * @param elements
	 * @param offset
	 * @param length
	 */
	public void addAllE(final int index, final E[] elements, final int offset, final int length) {
		if (index < 0 || index > this.size) {
			throw new IndexOutOfBoundsException("index= " + index + ", size= " + this.size); //$NON-NLS-1$ //$NON-NLS-2$
		}
		this.modCount++;
		ensureCapacity(this.size + length);
		if (index < this.size) {
			System.arraycopy(this.array, index, this.array, index + length, this.size - index);
		}
		System.arraycopy(elements, offset, this.array, index, length);
		this.size+= length;
	}
	
	@Override
	public E set(final int index, final E element) {
		checkIndex(index);
		final E previous= this.array[index];
		if (compare(previous, element) == 0) {
			this.modCount++;
			this.array[index]= element;
			return previous;
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	private void removeIndex(final int index) {
		this.modCount++;
		if (index + 1 < this.size) {
			System.arraycopy(this.array, index + 1, this.array, index, this.size - (index + 1));
		}
		this.array[--this.size]= null;
	}
	
	@Override
	protected void removeRange(final int fromIndex, final int toIndex) {
		final int n= toIndex - fromIndex;
		if (n > 0) {
			this.modCount++;
			System.arraycopy(this.array, toIndex, this.array, fromIndex, n);
			Arrays.fill(this.array, this.size - n, this.size, null);
			this.size= this.size - n;
		}
	}
	
	@Override
	public int removeE(final E element) {
		final int index= indexOfE(element);
		if (index >= 0) {
			removeIndex(index);
			return index;
		}
		return index;
	}
	
	@Override
	public boolean remove(final Object o) {
		return (removeE(castElementObject(o)) >= 0);
	}
	
	@Override
	public E remove(final int index) {
		checkIndex(index);
		final E previous= this.array[index];
		removeIndex(index);
		return previous;
	}
	
	@Override
	public void clear() {
		this.modCount++;
		for (int i= 0; i < this.size; i++) {
			this.array[i]= null;
		}
		this.size= 0;
	}
	
	
	@Override
	public SortedListSet<E> subList(final int fromIndex, final int toIndex) {
		if (fromIndex < 0 || fromIndex > this.size
				|| toIndex < 0 || toIndex > this.size) {
			throw new IndexOutOfBoundsException("fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		if (fromIndex > toIndex) {
			throw new IllegalArgumentException("fromIndex > toIndex: fromIndex= " + fromIndex + ", toIndex= " + toIndex); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return new SubList(fromIndex, toIndex - fromIndex);
	}
	
	
	@Override
	public Object[] toArray() {
		final Object[] a= new Object[this.size];
		System.arraycopy(this.array, 0, a, 0, a.length);
		return a;
	}
	
	@Override
	public <T> T[] toArray(final T[] a) {
		final int s= this.size;
		if (a.length < s) {
			return Arrays.<T, E>copyOf(this.array, s, (Class<T[]>) a.getClass());
		}
		System.arraycopy(this.array, 0, a, 0, s);
		if (a.length > s) {
			a[s]= null;
		}
		return a;
	}
	
}
