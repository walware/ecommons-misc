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

package de.walware.jcommons.collections.internal;

import java.util.Collection;
import java.util.ListIterator;

import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;


public abstract class AbstractImList<E> {
	
	
	protected static abstract class AbstractImListIter<E> implements ListIterator<E> {
		
		
		public AbstractImListIter() {
		}
		
		
		@Override
		public final void set(final E o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public final void add(final E o) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public final void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	public AbstractImList() {
	}
	
	
	public final E set(final int index, final E element) {
		throw new UnsupportedOperationException();
	}
	
	public final boolean add(final E o) {
		throw new UnsupportedOperationException();
	}
	
	public final void add(final int index, final E element) {
		throw new UnsupportedOperationException();
	}
	
	public final boolean addAll(final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	public final boolean addAll(final int index, final Collection<? extends E> c) {
		throw new UnsupportedOperationException();
	}
	
	public final boolean remove(final Object o) {
		throw new UnsupportedOperationException();
	}
	
	public final E remove(final int index) {
		throw new UnsupportedOperationException();
	}
	
	public final boolean removeAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	public final void clear() {
		throw new UnsupportedOperationException();
	}
	
	public final boolean retainAll(final Collection<?> c) {
		throw new UnsupportedOperationException();
	}
	
	
	public abstract E get(final int index);
	
	public abstract int indexOf(final Object o);
	
	public abstract int lastIndexOf(final Object o);
	
	
	public abstract void copyTo(Object[] dest, int destPos);
	
	public abstract void copyTo(int srcPos, Object[] dest, int destPos, int length);
	
	public abstract ImList<E> toImList();
	public abstract ImIdentityList<E> toImIdentityList();
	
}
