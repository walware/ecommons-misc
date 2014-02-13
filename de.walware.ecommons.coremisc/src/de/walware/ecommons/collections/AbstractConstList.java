/*=============================================================================#
 # Copyright (c) 2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.util.Collection;
import java.util.ListIterator;


public abstract class AbstractConstList<E> implements ConstList<E> {
	
	
	protected static abstract class AbstractConstListIter<E> implements ListIterator<E> {
		
		
		public AbstractConstListIter() {
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
		
	}
	
	
	public AbstractConstList() {
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
	
}
