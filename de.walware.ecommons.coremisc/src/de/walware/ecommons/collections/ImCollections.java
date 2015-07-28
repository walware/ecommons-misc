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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


/**
 * @since 1.2
 */
public class ImCollections {
	
	
	private static <E> boolean containsEqual(final E[] array, final int startIdx, final int endIdx, final E e) {
		if (e == null) {
			for (int idx= 0; idx < endIdx; idx++) {
				if (null == array[idx]) {
					return true;
				}
			}
			return false;
		}
		else {
			for (int idx= 0; idx < endIdx; idx++) {
				if (e.equals(array[idx])) {
					return true;
				}
			}
			return false;
		}
	}
	
	private static <E> boolean containsIdentical(final E[] array, final int startIdx, final int endIdx, final E e) {
		for (int idx= 0; idx < endIdx; idx++) {
			if (e == array[idx]) {
				return true;
			}
		}
		return false;
	}
	
	
/*[ List ]=====================================================================*/
	
	/**
	 * Returns an empty immutable list.
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> emptyList() {
		return ImEmptyList.INSTANCE;
	}
	
	
	/**
	 * Returns an empty immutable list.
	 * 
	 * Same as {@link #emptyList()}.
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> newList() {
		return ImEmptyList.INSTANCE;
	}
	
	/**
	 * Creates a new immutable list containing the specified element.
	 * 
	 * @param e the element
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> newList(final E e) {
		return new ImSingletonList<>(e);
	}
	
	/**
	 * Creates a new immutable list containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the list; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e the elements
	 * 
	 * @return the immutable list
	 */
	@SafeVarargs
	public static <E> ImList<E> newList(final E... e) {
		if (e.length == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (e.length == 1) {
			return new ImSingletonList<>(e[0]);
		}
		else {
			return new ImArrayList<>(e);
		}
	}
	
	/**
	 * Creates a new immutable list containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the list; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e array containing the elements
	 * @param startIdx index of first element in the array
	 * @param length of new array
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> newList(final E[] e, final int startIdx, final int length) {
		if (length == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (length == 1) {
			return new ImSingletonList<>(e[startIdx]);
		}
		else if (length == e.length && startIdx == 0) {
			return new ImArrayList<>(e);
		}
		else {
			return new ImArrayList<>(Arrays.copyOfRange(e, startIdx, startIdx + length));
		}
	}
	
	
	/**
	 * Returns a immutable list containing the elements of the specified collection.
	 * 
	 * If the collection is already an immutable list, the method return the same instance.
	 * 
	 * @param c collection containing the elements
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> toList(final Collection<? extends E> c) {
		if (c instanceof AbstractImList) {
			return ((AbstractImList<E>) c).toImList();
		}
		final int n= c.size();
		if (n == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonList<>((c instanceof List) ?
					((List<E>) c).get(0) : ((E[]) c.toArray())[0] );
		}
		else {
			return new ImArrayList<>((E[]) c.toArray());
		}
	}
	
	/**
	 * Returns a immutable list containing the elements of the iterable object.
	 * 
	 * If the collection is already an immutable list, the method return the same instance.
	 * 
	 * @param iterable object with iterator for the elements
	 * 
	 * @return the immutable list
	 */
	public static <E> ImList<E> toList(final Iterable<? extends E> iterable) {
		if (iterable instanceof Collection) {
			return toList((Collection<? extends E>) iterable);
		}
		final Iterator<? extends E> iter= iterable.iterator();
		if (!iter.hasNext()) {
			return ImEmptyList.INSTANCE;
		}
		final E first= iter.next();
		if (!iter.hasNext()) {
			return new ImSingletonList<>(first);
		}
		else {
			final List<E> list= new ArrayList<>();
			list.add(first);
			do {
				list.add(iter.next());
			}
			while (iter.hasNext());
			return new ImArrayList<>((E[]) list.toArray());
		}
	}
	
	
	private static void copyTo(final List<?> src, final Object[] dest, final int destPos) {
		if (src instanceof AbstractImList<?>) {
			((AbstractImList<?>) src).copyTo(dest, destPos);
		}
		else if (destPos == 0) {
			src.toArray(dest);
		}
		else {
			final Object[] a= src.toArray();
			System.arraycopy(a, 0, dest, destPos, a.length);
		}
	}
	
	public static <T> ImList<T> concatList(final List<? extends T> l1, final List<? extends T> l2) {
		final int n1= l1.size();
		final int n= n1 + l2.size();
		if (n == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (n1 == 0) {
			return toList(l2);
		}
		else if (n == n1) { // n2 == 0
			return toList(l1);
		}
		else {
			final Object[] a= new Object[n];
			copyTo(l1, a, 0);
			copyTo(l2, a, n1);
			return new ImArrayList<>((T[]) a);
		}
	}
	
	public static <T> ImList<T> concatList(final List<? extends T> l1, final List<? extends T> l2, final List<? extends T> l3) {
		final int n1= l1.size();
		final int n12= n1 + l2.size();
		final int n= n12 + l3.size();
		if (n == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (n12 == 0) { // n1 == 0 && n2 == 0
			return toList(l3);
		}
		else if (n == n1) { // n2 == 0 && n3 == 0
			return toList(l1);
		}
		else if (n1 == 0 && n == n12) { // n1 == 0 && n3 == 0
			return toList(l2);
		}
		else {
			final Object[] a= new Object[n];
			copyTo(l1, a, 0);
			copyTo(l2, a, n1);
			copyTo(l3, a, n12);
			return new ImArrayList<>((T[]) a);
		}
	}
	
	@SafeVarargs
	public static <T> ImList<T> concatList(final List<? extends T>... lists) {
		switch (lists.length) {
		case 0:
			return ImEmptyList.INSTANCE;
		case 1:
			return toList(lists[0]);
		case 2:
			return concatList(lists[0], lists[1]);
		case 3:
			return concatList(lists[0], lists[1], lists[2]);
		default:
			int n= 0;
			for (int i= 0; i < lists.length; i++) {
				n+= lists[i].size();
			}
			final Object[] a= new Object[n];
			n= 0;
			for (int i= 0; i < lists.length; i++) {
				copyTo(lists[i], a, n);
				n+= lists[i].size();
			}
			return newList((T[]) a);
		}
	}
	
	
	public static <T> ImList<T> addElement(final List<? extends T> l, final T e) {
		final int n1= l.size();
		if (n1 == 0) {
			return new ImSingletonList<>(e);
		}
		else {
			final Object[] a= new Object[n1 + 1];
			copyTo(l, a, 0);
			a[n1]= e;
			return new ImArrayList<>((T[]) a);
		}
	}
	
	public static <T> ImList<T> addElement(final List<? extends T> l, final int index, final T e) {
		final int n1= l.size();
		if (n1 == 0) {
			return new ImSingletonList<>(e);
		}
		else {
			final Object[] a= new Object[n1 + 1];
			if (index == 0) {
				copyTo(l, a, 1);
			}
			else if (index == n1) {
				copyTo(l, a, 0);
			}
			else if (l instanceof AbstractImList) {
				((AbstractImList) l).copyTo(0, a, 0, index);
				((AbstractImList) l).copyTo(index, a, index + 1, n1 - index);
			}
			else {
				copyTo(l, a, 0);
				System.arraycopy(a, index, a, index + 1, n1 - index);
			}
			a[index]= e;
			return new ImArrayList<>((T[]) a);
		}
	}
	
	public static <T> ImList<T> removeElement(final List<? extends T> l, final Object e) {
		final int idx= l.indexOf(e);
		if (idx < 0) {
			return toList(l);
		}
		final int n= l.size() - 1;
		if (n == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonList<T>(l.get((idx == 0) ? 1 : 0));
		}
		else {
			final Object[] a= new Object[n];
			if (l instanceof AbstractImList<?>) {
				if (idx > 0) {
					((AbstractImList<?>) l).copyTo(0, a, 0, idx);
				}
				if (idx < n) {
					((AbstractImList<?>) l).copyTo(idx + 1, a, idx, n - idx);
				
				}
			}
			else {
				final Object[] src= l.toArray();
				if (idx > 0) {
					System.arraycopy(src, 0, a, 0, idx);
				}
				if (idx < n) {
					System.arraycopy(src, idx + 1, a, idx, n - idx);
				}
			}
			return new ImArrayList<>((T[]) a);
		}
	}
	
	
/*[ IdentityList ]=============================================================*/
	
	/**
	 * Returns an empty immutable list.
	 */
	public static <E> ImIdentityList<E> newIdentityList() {
		return ImEmptyIdentityList.INSTANCE;
	}
	
	/**
	 * Creates a new immutable list containing the specified element.
	 * 
	 * @param e the element
	 */
	public static <E> ImIdentityList<E> newIdentityList(final E e) {
		return new ImSingletonIdentityList<>(e);
	}
	
	/**
	 * Creates a new immutable list containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the list; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e the elements
	 */
	@SafeVarargs
	public static <E> ImIdentityList<E> newIdentityList(final E... e) {
		if (e.length == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (e.length == 1) {
			return new ImSingletonIdentityList<>(e[0]);
		}
		else {
			return new ImArrayIdentityList<>(e);
		}
	}
	
	/**
	 * Creates a new immutable list containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the list; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e array containing the elements
	 * @param startIdx index of first element in the array
	 * @param length of new array
	 */
	public static <E> ImIdentityList<E> newIdentityList(final E[] e, final int startIdx, final int length) {
		if (length == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (length == 1) {
			return new ImSingletonIdentityList<>(e[startIdx]);
		}
		else if (length == e.length && startIdx == 0) {
			return new ImArrayIdentityList<>(e);
		}
		else {
			return new ImArrayIdentityList<>(Arrays.copyOfRange(e, startIdx, startIdx + length));
		}
	}
	
	
	public static <E> ImIdentityList<E> toIdentityList(final Collection<? extends E> c) {
		if (c instanceof AbstractImList) {
			return ((AbstractImList<E>) c).toImIdentityList();
		}
		final int n= c.size();
		if (n == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonIdentityList<>((c instanceof List) ?
					((List<E>) c).get(0) : c.iterator().next() );
		}
		else {
			return new ImArrayIdentityList<>((E[]) c.toArray());
		}
	}
	
	
	public static <T> ImIdentityList<T> concatList(final IdentityList<? extends T> l1, final IdentityList<? extends T> l2) {
		final int n1= l1.size();
		final int n= n1 + l2.size();
		if (n == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (n1 == 0) {
			return toIdentityList(l2);
		}
		else if (n == n1) { // n2 == 0
			return toIdentityList(l1);
		}
		else {
			final Object[] a= new Object[n];
			copyTo(l1, a, 0);
			copyTo(l2, a, n1);
			return new ImArrayIdentityList<>((T[]) a);
		}
	}
	
	
	public static <T> ImIdentityList<T> addElement(final IdentityList<? extends T> l, final T e) {
		if (l.isEmpty()) {
			return new ImSingletonIdentityList<>(e);
		}
		else {
			final Object[] a= new Object[l.size() + 1];
			copyTo(l, a, 0);
			a[l.size()]= e;
			return new ImArrayIdentityList<>((T[]) a);
		}
	}
	
	public static <T> ImIdentityList<T> removeElement(final IdentityList<? extends T> l, final Object e) {
		final int idx= l.indexOf(e);
		if (idx < 0) {
			return toIdentityList(l);
		}
		final int n= l.size() - 1;
		if (n == 0) {
			return ImEmptyIdentityList.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonIdentityList<T>(l.get((idx == 0) ? 1 : 0));
		}
		else {
			final Object[] a= new Object[n];
			if (l instanceof AbstractImList<?>) {
				if (idx > 0) {
					((AbstractImList<?>) l).copyTo(0, a, 0, idx);
				}
				if (idx < n) {
					((AbstractImList<?>) l).copyTo(idx + 1, a, idx, n - idx);
				
				}
			}
			else {
				final Object[] src= l.toArray();
				if (idx > 0) {
					System.arraycopy(src, 0, a, 0, idx);
				}
				if (idx < n) {
					System.arraycopy(src, idx + 1, a, idx, n - idx);
				}
			}
			return new ImArrayIdentityList<>((T[]) a);
		}
	}
	
	
/*[ Set ]======================================================================*/
	
	/**
	 * Returns an empty immutable set.
	 */
	public static <E> ImSet<E> emptySet() {
		return ImEmptySet.INSTANCE;
	}
	
	
	/**
	 * Returns an empty immutable set.
	 * 
	 * Same as {@link #emptySet()}.
	 */
	public static <E> ImSet<E> newSet() {
		return ImEmptySet.INSTANCE;
	}
	
	/**
	 * Creates a new immutable set containing the specified element.
	 * 
	 * @param e the element
	 */
	public static <E> ImSet<E> newSet(final E e) {
		return new ImSingletonSet<>(e);
	}
	
	
	private static <E> ImSet<E> newSetFromArray(final E[] array, final int startIdx, final int endIdx) {
		for (int idx= startIdx + 1; idx < endIdx; idx++) {
			if (containsEqual(array, startIdx, idx, array[idx])) {
				final E[] checked= (E[]) Array.newInstance(array.getClass().getComponentType(),
						endIdx - startIdx - 1 );
				System.arraycopy(array, startIdx, checked, 0, idx - startIdx);
				int length= idx++;
				for (; idx < endIdx; idx++) {
					if (!containsEqual(checked, 0, length, array[idx])) {
						checked[length++]= array[idx];
					}
				}
				
				if (length == 1) {
					return new ImSingletonSet<>(checked[0]);
				}
				else if (length == checked.length) {
					return new ImArraySet<>(checked);
				}
				else {
					return new ImArraySet<>(Arrays.copyOfRange(checked, 0, length));
				}
			}
		}
		return new ImArraySet<>(array);
	}
	
	/**
	 * Creates a new immutable set containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the set; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e the elements
	 */
	@SafeVarargs
	public static <E> ImSet<E> newSet(final E... e) {
		if (e.length == 0) {
			return ImEmptySet.INSTANCE;
		}
		else if (e.length == 1) {
			return new ImSingletonSet<>(e[0]);
		}
		else {
			return newSetFromArray(e, 0, e.length);
		}
	}
	
	/**
	 * Creates a new immutable set containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the set; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e array containing the elements
	 * @param startIdx index of first element in the array
	 * @param length of new array
	 */
	public static <E> ImSet<E> newSet(final E[] e, final int startIdx, final int length) {
		if (length == 0) {
			return ImEmptySet.INSTANCE;
		}
		else if (length == 1) {
			return new ImSingletonSet<>(e[startIdx]);
		}
		else {
			return newSetFromArray(e, startIdx, startIdx + length);
		}
	}
	
	
	public static <E> ImSet<E> toSet(final Collection<? extends E> c) {
		if (c instanceof ImSet) {
			return (ImSet<E>) c;
		}
		final int n= c.size();
		if (n == 0) {
			return ImEmptySet.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonSet<>((c instanceof List) ?
					((List<E>) c).get(0) : c.iterator().next() );
		}
		else {
			return newSetFromArray((E[]) c.toArray(), 0, n);
		}
	}
	
	
/*[ IdentitySet ]==============================================================*/
	
	/**
	 * Returns an empty immutable set.
	 */
	public static <E> ImIdentitySet<E> emptyIdentitySet() {
		return ImEmptyIdentitySet.INSTANCE;
	}
	
	
	/**
	 * Returns an empty immutable set.
	 * 
	 * Same as {@link #emptySet()}.
	 */
	public static <E> ImIdentitySet<E> newIdentitySet() {
		return ImEmptyIdentitySet.INSTANCE;
	}
	
	/**
	 * Creates a new immutable set containing the specified element.
	 * 
	 * @param e the element
	 */
	public static <E> ImIdentitySet<E> newIdentitySet(final E e) {
		return new ImSingletonIdentitySet<>(e);
	}
	
	
	private static <E> ImIdentitySet<E> newIdentitySetFromArray(final E[] array, final int startIdx, final int endIdx) {
		for (int idx= startIdx + 1; idx < endIdx; idx++) {
			if (containsIdentical(array, startIdx, idx, array[idx])) {
				final E[] checked= (E[]) Array.newInstance(array.getClass().getComponentType(),
						endIdx - startIdx - 1 );
				System.arraycopy(array, startIdx, checked, 0, idx - startIdx);
				int length= idx++;
				for (; idx < endIdx; idx++) {
					if (!containsIdentical(checked, 0, length, array[idx])) {
						checked[length++]= array[idx];
					}
				}
				
				if (length == 1) {
					return new ImSingletonIdentitySet<>(checked[0]);
				}
				else if (length == checked.length) {
					return new ImArrayIdentitySet<>(checked);
				}
				else {
					return new ImArrayIdentitySet<>(Arrays.copyOfRange(checked, 0, length));
				}
			}
		}
		return new ImArrayIdentitySet<>(array);
	}
	
	/**
	 * Creates a new immutable set containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the set; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e the elements
	 */
	@SafeVarargs
	public static <E> ImIdentitySet<E> newIdentitySet(final E... e) {
		if (e.length == 0) {
			return ImEmptyIdentitySet.INSTANCE;
		}
		else if (e.length == 1) {
			return new ImSingletonIdentitySet<>(e[0]);
		}
		else {
			return newIdentitySetFromArray(e, 0, e.length);
		}
	}
	
	/**
	 * Creates a new immutable set containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the set; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e array containing the elements
	 * @param startIdx index of first element in the array
	 * @param length of new array
	 */
	public static <E> ImIdentitySet<E> newIdentitySet(final E[] e, final int startIdx, final int length) {
		if (length == 0) {
			return ImEmptyIdentitySet.INSTANCE;
		}
		else if (length == 1) {
			return new ImSingletonIdentitySet<>(e[startIdx]);
		}
		else {
			return newIdentitySetFromArray(e, startIdx, startIdx + length);
		}
	}
	
	
	public static <E> ImIdentitySet<E> toIdentitySet(final Collection<? extends E> c) {
		if (c instanceof ImSet) {
			return (ImIdentitySet<E>) c;
		}
		final int n= c.size();
		if (n == 0) {
			return ImEmptyIdentitySet.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonIdentitySet<>((c instanceof List) ?
					((List<E>) c).get(0) : c.iterator().next() );
		}
		else {
			return newIdentitySetFromArray((E[]) c.toArray(), 0, n);
		}
	}
	
}
