/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.collections;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;


/**
 * Thread safe set based on immutable lists.
 * 
 * @param <E>
 */
public final class CopyOnWriteListSet<E> extends AbstractSet<E> implements Set<E> {
	
	
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
	
	
	public CopyOnWriteListSet() {
		this.list= ImCollections.newList();
	}
	
	public CopyOnWriteListSet(final Set<E> initialSet) {
		this.list= ImCollections.toIdentityList(initialSet);
	}
	
	
	@Override
	public synchronized boolean add(final E element) {
		if (!this.list.contains(element)) {
			this.list= ImCollections.addElement(this.list, element);
			return true;
		}
		return false;
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
	public synchronized boolean addAll(final Collection<? extends E> c) {
		final ImList<E> l= this.list;
		final Object[] toAdd= c.toArray();
		int end= toAdd.length;
		for (int i= 0; i < end; ) {
			if (l.contains(toAdd[i]) || indexOf(toAdd, toAdd[i]) < i) {
				System.arraycopy(toAdd, i + 1, toAdd, i, end - i - 1);
				end--;
			}
			else {
				i++;
			}
		}
		if (end > 0) {
			this.list= ImCollections.concatList(l,
					ImCollections.<E>newList((E[]) toAdd, 0, end) );
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
	public boolean contains(final Object o) {
		return this.list.contains(o);
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		return this.list.containsAll(c);
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return this.list.iterator();
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
	 * Returns a current snapshot of the set.
	 * 
	 * @return
	 */
	public ImList<E> toList() {
		return this.list;
	}
	
	/**
	 * Returns a current snapshot of the list and clears the list.
	 * 
	 * @return
	 */
	public synchronized ImList<E> clearToList() {
		final ImList<E> list= this.list;
		if (!list.isEmpty()) {
			this.list= ImCollections.newList();
		}
		return list;
	}
	
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof Set) {
			final Set<?> other= (Set<?>) obj;
			final ImList<E> l= this.list;
			return (l.size() == other.size()
					&& l.containsAll(other) );
		}
		return false;
	}
	
}
