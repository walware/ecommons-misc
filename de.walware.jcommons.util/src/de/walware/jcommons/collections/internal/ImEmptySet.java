/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.Set;

import de.walware.jcommons.collections.ImIdentityList;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;


/**
 * Empty set implementation.
 * <p>
 * Comparable to <code>Collections.emptySet()</code>.</p>
 * 
 * @since 1.5
 */
public final class ImEmptySet<E> extends AbstractImList<E> implements ImSet<E>,
		RandomAccess {
	
	
	@SuppressWarnings("rawtypes")
	public static final ImEmptySet INSTANCE= new ImEmptySet();
	
	
	private final static Object[] ARRAY= new Object[0];
	
	private final ListIterator<E> iterator= new AbstractImListIter<E>() {
		
		@Override
		public boolean hasNext() {
			return false;
		}
		
		@Override
		public int nextIndex() {
			return 0;
		}
		
		@Override
		public E next() {
			throw new NoSuchElementException();
		}
		
		@Override
		public boolean hasPrevious() {
			return false;
		}
		
		@Override
		public int previousIndex() {
			return -1;
		}
		
		@Override
		public E previous() {
			throw new NoSuchElementException();
		}
		
	};
	
	
	public ImEmptySet() {
	}
	
	
	@Override
	public int size() {
		return 0;
	}
	
	@Override
	public boolean isEmpty() {
		return true;
	}
	
	@Override
	public boolean contains(final Object o) {
		return false;
	}
	
	@Override
	public boolean containsAll(final Collection<?> c) {
		return c.isEmpty();
	}
	
	@Override
	public E get(final int index) {
		throw new IndexOutOfBoundsException("index= " + index); //$NON-NLS-1$
	}
	
	@Override
	public int indexOf(final Object o) {
		return -1;
	}
	
	@Override
	public int lastIndexOf(final Object o) {
		return -1;
	}
	
	
	@Override
	public Iterator<E> iterator() {
		return this.iterator;
	}
	
	
	@Override
	public Object[] toArray() {
		return ARRAY;
	}
	
	@Override
	public <T> T[] toArray(final T[] dest) {
		if (dest.length > 0) {
			dest[0]= null;
		}
		return dest;
	}
	
	@Override
	public
	void copyTo(final Object[] dest, final int destPos) {
	}
	
	@Override
	public
	void copyTo(final int srcPos, final Object[] dest, final int destPos, final int length) {
		assert (false);
	}
	
	@Override
	public
	ImList<E> toImList() {
		return ImEmptyList.INSTANCE;
	}
	
	@Override
	public
	ImIdentityList<E> toImIdentityList() {
		return ImEmptyIdentityList.INSTANCE;
	}
	
	
	@Override
	public int hashCode() {
		return 0;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof Set) {
			final Set<?> other= (Set<?>) obj;
			return (other.isEmpty());
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "[]"; //$NON-NLS-1$
	}
	
}
