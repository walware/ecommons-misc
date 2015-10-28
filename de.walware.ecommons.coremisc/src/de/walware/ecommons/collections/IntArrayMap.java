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

package de.walware.ecommons.collections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;
import java.util.Set;


/**
 * An object that maps integer keys to values using the integer value as array index
 * <p>
 * Because it uses the integer value as array index, the map is only appropriate for lower
 * positive values.</p>
 * 
 * @param <V> type of the values
 * @since 1.0
 */
public final class IntArrayMap<V> implements IntMap<V> {
	
	
	private Object[] fArray;
	
	private int fSize;
	
	
	public IntArrayMap() {
		fArray = new Object[16];
	}
	
	public IntArrayMap(final int initialCapacity) {
		fArray = new Object[initialCapacity];
	}
	
	
	@Override
	public boolean isEmpty() {
		return (fSize == 0);
	}
	
	@Override
	public int size() {
		return fSize;
	}
	
	@Override
	public boolean containsKey(final int key) {
		return (key < fArray.length && fArray[key] != null);
	}
	
	@Override
	public boolean containsKey(final Object key) {
		return ((key instanceof Integer) && containsKey(((Integer) key).intValue()));
	}
	
	@Override
	public boolean containsValue(final Object value) {
		for (int i = fArray.length-1; i >= 0; i--) {
			if (fArray[i] != null && fArray[i].equals(value)) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public V get(final int key) {
		if (key < fArray.length) {
			return (V) fArray[key];
		}
		return null;
	}
	
	@Override
	public V get(final Object key) {
		return (key instanceof Integer) ? get(((Integer) key).intValue()) : null;
	}
	
	
	private void increase(final int min) {
		final Object[] newArray = new Object[min+16];
		System.arraycopy(fArray, 0, newArray, 0, fArray.length);
		fArray = newArray;
	}
	
	@Override
	public V put(final int key, final V value) {
		if (key >= fArray.length) {
			increase(key);
		}
		final V oldValue = (V) fArray[key];
		fArray[key] = value;
		if (oldValue != null) {
			return oldValue;
		}
		else {
			fSize++;
			return null;
		}
	}
	
	@Override
	public V put(final Integer key, final V value) {
		return put(key.intValue(), value);
	}
	
	@Override
	public void putAll(final Map<? extends Integer, ? extends V> t) {
		for (final java.util.Map.Entry<? extends Integer, ? extends V> entry : t.entrySet()) {
			put(entry.getKey().intValue(), entry.getValue());
		}
	}
	
	public V remove(final int key) {
		if (key < fArray.length) {
			final V oldValue = (V) fArray[key];
			if (oldValue != null) {
				fArray[key] = null;
				fSize--;
				return oldValue;
			}
		}
		return null;
	}
	
	@Override
	public V remove(final Object key) {
		return (key instanceof Integer) ? remove(((Integer) key).intValue()) : null;
	}
	
	@Override
	public void clear() {
		if (fSize > 0) {
			for (int i = fArray.length-1; i >= 0; i--) {
				fArray[i] = null;
			}
			fSize = 0;
		}
	}
	
	
	@Override
	public Set<Integer> keySet() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Collection<V> values() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public Set<java.util.Map.Entry<Integer, V>> entrySet() {
		throw new UnsupportedOperationException();
	}
	
	
	public int getMaxKey() {
		if (fSize > 0) {
			for (int i = fArray.length-1; i >= 0; i--) {
				if (fArray[i] != null) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public V[] toArray(final Class<? super V> type) {
		final int length = getMaxKey() + 1;
		final V[] array = (V[]) Array.newInstance(type, length);
		System.arraycopy(fArray, 0, array, 0, length);
		return array;
	}
	
}
