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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;


/**
 * @since 1.2
 */
public class ImCollections {
	
	
	/**
	 * Returns an empty immutable list.
	 */
	public static <E> ImList<E> emptyList() {
		return ImEmptyList.INSTANCE;
	}
	
	
	/**
	 * Returns an empty immutable list.
	 * 
	 * Same as {@link #emptyList()}.
	 */
	public static <E> ImList<E> newList() {
		return ImEmptyList.INSTANCE;
	}
	
	/**
	 * Creates a new immutable list containing the specified element.
	 * 
	 * @param e the element
	 */
	public static <E> ImList<E> newList(final E e) {
		return new ImSingletonList<E>(e);
	}
	
	/**
	 * Creates a new immutable list containing the specified elements.
	 * 
	 * NOTE: If the elements are specified by an array, the array may be reused by the list; the 
	 * array must not any longer be changed to fulfill the immutability.
	 * 
	 * @param e the elements
	 */
	public static <E> ImList<E> newList(final E... e) {
		if (e.length == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (e.length == 1) {
			return new ImSingletonList<E>(e[0]);
		}
		else {
			return new ImArrayList<E>(e);
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
	public static <E> ImList<E> newList(final E[] e, final int startIdx, final int length) {
		if (length == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (length == 1) {
			return new ImSingletonList<E>(e[startIdx]);
		}
		else if (length == e.length && startIdx == 0) {
			return new ImArrayList<E>(e);
		}
		else {
			return new ImArrayList<E>(Arrays.copyOfRange(e, startIdx, startIdx + length));
		}
	}
	
	
	public static <E> ImList<E> toList(final Collection<? extends E> c) {
		if (c instanceof ImList) {
			return (ImList<E>) c;
		}
		final int n= c.size();
		if (n == 0) {
			return ImEmptyList.INSTANCE;
		}
		else if (n == 1) {
			return new ImSingletonList<E>((c instanceof List) ?
					((List<E>) c).get(0) : c.iterator().next() );
		}
		else {
			return new ImArrayList<E>((E[]) c.toArray());
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
			return new ImArrayList<T>((T[]) a);
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
			return new ImArrayList<T>((T[]) a);
		}
	}
	
	public static <T> ImList<T> concatList(final T e1, final List<? extends T> l2) {
		if (l2.isEmpty()) {
			return new ImSingletonList<T>(e1);
		}
		else {
			final Object[] a= new Object[l2.size() + 1];
			a[0]= e1;
			copyTo(l2, a, 1);
			return new ImArrayList<T>((T[]) a);
		}
	}
	
	public static <T> ImList<T> concatList(final List<? extends T> l1, final T e2) {
		if (l1.isEmpty()) {
			return new ImSingletonList<T>(e2);
		}
		else {
			final Object[] a= new Object[l1.size() + 1];
			copyTo(l1, a, 0);
			a[l1.size()]= e2;
			return new ImArrayList<T>((T[]) a);
		}
	}
	
	
	public static <T> ImList<T> addElement(final List<? extends T> l, final T e) {
		if (l.isEmpty()) {
			return new ImSingletonList<T>(e);
		}
		else {
			final Object[] a= new Object[l.size() + 1];
			copyTo(l, a, 0);
			a[l.size()]= e;
			return new ImArrayList<T>((T[]) a);
		}
	}
	
	public static <T> ImList<T> removeElement(final List<? extends T> l, final T e) {
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
			return new ImArrayList<T>((T[]) a);
		}
	}
	
}
