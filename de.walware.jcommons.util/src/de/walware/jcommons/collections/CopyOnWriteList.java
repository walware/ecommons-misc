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

package de.walware.jcommons.collections;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;


/**
 * Thread safe list based on immutable lists.
 * 
 * @param <E>
 */
public final class CopyOnWriteList<E> extends AbstractList<E> implements List<E> {
	
	
	private static int indexOf(final Object[] array, final Object e) {
		if (e == null) {
			for (int i= 0; i < array.length; i++) {
				if (null == array[i]) {
					return i;
				}
			}
			return -1;
		}
		else {
			for (int i= 0; i < array.length; i++) {
				if (e.equals(array[i])) {
					return i;
				}
			}
			return -1;
		}
	}
	
	
	private volatile ImList<E> list;
	
	
	public CopyOnWriteList() {
		this.list= ImCollections.newList();
	}
	
	public CopyOnWriteList(final List<E> initialList) {
		this.list= ImCollections.toList(initialList);
	}
	
	
	@Override
	public synchronized boolean add(final E element) {
		this.list= ImCollections.addElement(this.list, element);
		return true;
	}
	
	@Override
	public synchronized void add(final int index, final E element) {
		this.list= ImCollections.addElement(this.list, index, element);
	}
	
	@Override
	public synchronized E set(final int index, final E element) {
		final ImList<E> l= this.list;
		this.list= ImCollections.setElement(l, index, element);
		return l.get(index);
	}
	
	@Override
	public synchronized boolean remove(final Object element) {
		final ImList<E> l= ImCollections.removeElement(this.list, element);
		if (l != this.list) {
			this.list= l;
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized E remove(final int index) {
		final ImList<E> l= this.list;
		this.list= ImCollections.removeElement(l, index);
		return l.get(index);
	}
	
	@Override
	public synchronized boolean addAll(final Collection<? extends E> c) {
		if (!c.isEmpty()) {
			this.list= ImCollections.concatList(this.list, c);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean addAll(final int index, final Collection<? extends E> c) {
		if (!c.isEmpty()) {
			final ImList<E> l= this.list;
			this.list= ImCollections.concatList(l.subList(0, index), c, l.subList(index, l.size()));
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean retainAll(final Collection<?> c) {
		final Object[] array= this.list.toArray();
		final Object[] toRetain= c.toArray();
		int end= array.length;
		for (int i= 0; i < end; ) {
			if (indexOf(toRetain, array[i]) >= 0) {
				i++;
			}
			else {
				System.arraycopy(array, i + 1, array, i, end - i - 1);
				end--;
			}
		}
		if (end < array.length) {
			this.list= ImCollections.<E>newList((E[]) array, 0, end);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized boolean removeAll(final Collection<?> c) {
		final Object[] array= this.list.toArray();
		final Object[] toRemove= c.toArray();
		int end= array.length;
		for (int i= 0; i < end; ) {
			if (indexOf(toRemove, array[i]) >= 0) {
				i++;
			}
			else {
				System.arraycopy(array, i + 1, array, i, end - i - 1);
				end--;
			}
		}
		if (end < array.length) {
			this.list= ImCollections.<E>newList((E[]) array, 0, end);
			return true;
		}
		return false;
	}
	
	@Override
	public synchronized void clear() {
		if (!this.list.isEmpty()) {
			this.list= ImCollections.newList();
		}
	}
	
	
	@Override
	public int size() {
		return this.list.size();
	}
	
	@Override
	public boolean isEmpty() {
		return this.list.isEmpty();
	}
	
	@Override
	public E get(final int index) {
		return this.list.get(index);
	}
	
	@Override
	public boolean contains(final Object o) {
		return this.list.contains(o);
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.list.containsAll(c);
	}
	
	@Override
	public int indexOf(final Object o) {
		return this.list.indexOf(o);
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		return this.list.lastIndexOf(o);
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return this.list.iterator();
	}
	
	@Override
	public ListIterator<E> listIterator() {
		return this.list.listIterator();
	}
	
	@Override
	public ListIterator<E> listIterator(final int index) {
		return this.list.listIterator(index);
	}
	
	
	@Override
	public Object[] toArray() {
		return this.list.toArray();
	}
	
	@Override
	public <T> T[] toArray(final T[] a) {
		return this.list.toArray(a);
	}
	
	/**
	 * Returns a current snapshot of the list.
	 * 
	 * @return
	 */
	public ImList<E> toList() {
		return this.list;
	}
	
	
	@Override
	public int hashCode() {
		return this.list.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		return (this == obj || this.list.equals(obj));
	}
	
	@Override
	public String toString() {
		return this.list.toString();
	}
	
}
