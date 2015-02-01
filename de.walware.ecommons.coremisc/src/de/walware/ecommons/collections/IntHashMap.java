/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.nio.channels.UnsupportedAddressTypeException;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;


/**
 * An object that maps integer keys to values using the integer value itself as hash value.
 * 
 * @param <V> type of the values
 * @since 1.0
 */
public final class IntHashMap<V> implements IntMap<V> {
	
	
	private static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	private static class Entry<V> implements java.util.Map.Entry<Integer, V>, IntEntry<V> {
		
		final int key;
		V value;
		Entry<V> next;
		
		public Entry(final int key, final V value, final Entry<V> next) {
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		
		@Override
		public int getIntKey() {
			return this.key;
		}
		
		@Override
		public Integer getKey() {
			return Integer.valueOf(this.key);
		}
		
		@Override
		public V getValue() {
			return this.value;
		}
		
		@Override
		public V setValue(final V value) {
			final V oldValue = this.value;
			this.value = value;
			return oldValue;
		}
		
		@Override
		public int hashCode() {
			return this.key + ((this.value != null) ? this.value.hashCode() : 0);
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof IntEntry)) {
				return false;
			}
			final IntEntry<?> other = (de.walware.ecommons.collections.IntMap.IntEntry<?>) obj;
			return (this.key == other.getIntKey()
					&& ((this.value != null) ? this.value.equals(other.getValue()) : null == other.getValue()) );
		}
		
	}
	
	private class IntEntryIterator implements Iterator<IntEntry<V>> {
		
		private Entry<V> fCurrentEntry;
		private Entry<V> fNextEntry;
		private int fNextEntryIdx;
		
		public IntEntryIterator() {
			for (int idx = 0; idx < fEntries.length; idx++) {
				if (fEntries[idx] != null) {
					fNextEntry = fEntries[fNextEntryIdx = idx];
					break;
				}
			}
		}
		
		@Override
		public boolean hasNext() {
			return (fNextEntry != null);
		}
		
		@Override
		public Entry<V> next() {
			fCurrentEntry = fNextEntry;
			if (fCurrentEntry == null) {
				throw new NoSuchElementException();
			}
			if (fCurrentEntry.next != null) {
				fNextEntry = fCurrentEntry.next;
			}
			else {
				fNextEntry = null;
				for (int idx = fNextEntryIdx+1; idx < fEntries.length; idx++) {
					if (fEntries[idx] != null) {
						fNextEntry = fEntries[fNextEntryIdx = idx];
						break;
					}
				}
			}
			return fCurrentEntry;
		}
		
		@Override
		public void remove() {
			if (fCurrentEntry == null) {
				throw new IllegalStateException();
			}
			IntHashMap.this.remove(fCurrentEntry.key);
			fCurrentEntry = null;
		}
		
	}
	
	
	private Entry<V> fEntries[];
	
	private int fSize;
	
	private int fThreshold;
	
	private final float fLoadFactor;
	
	private volatile Set<IntEntry<V>> fEntryIntSet;
	
	
	public IntHashMap() {
		this(16, DEFAULT_LOAD_FACTOR);
	}
	
	public IntHashMap(final int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}
	
	public IntHashMap(int initialCapacity, final float loadFactor) {
		if (initialCapacity < 0) {
			throw new IllegalArgumentException("initialCapacity: " + initialCapacity);
		}
		if (loadFactor <= 0) {
			throw new IllegalArgumentException("loadFactor: " + loadFactor);
		}
		if (initialCapacity == 0) {
			initialCapacity = 1;
		}
		
		fLoadFactor = loadFactor;
		fThreshold = (int) (initialCapacity * fLoadFactor);
		fEntries = new Entry[initialCapacity];
	}
	
	
	private int idxFor(final int key) {
		final int compr = key ^ (key >>> 23) ^ (key >>> 11);
		return ((compr ^ (compr >>> 7)) & 0x7fffffff) % fEntries.length;
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
		for (Entry<V> e = fEntries[idxFor(key)]; e != null; e = e.next) {
			if (e.key == key) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean containsKey(final Object key) {
		return (key instanceof Integer
				&& containsKey(((Integer) key).intValue()) );
	}
	
	@Override
	public boolean containsValue(final Object value) {
		for (int i = fEntries.length-1; i >= 0; i--) {
			for (Entry<V> e = fEntries[i]; e != null; e = e.next) {
				if (e.value.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}
	
	@Override
	public V get(final int key) {
		for (Entry<V> e = fEntries[idxFor(key)]; e != null; e = e.next) {
			if (e.key == key) {
				return e.value;
			}
		}
		return null;
	}
	
	@Override
	public V get(final Object key) {
		return (key instanceof Integer) ? get(((Integer) key).intValue()) : null;
	}
	
	
	private void increase() {
		final Entry<V>[] oldEntries = fEntries;
		fEntries = new Entry[oldEntries.length * 2 + 1];
		for (int i = oldEntries.length-1; i >= 0; i--) {
			for (Entry<V> next = oldEntries[i]; next != null;) {
				final Entry<V> e = next;
				next = next.next;
				
				final int idx = idxFor(e.key);
				e.next = fEntries[idx];
				fEntries[idx] = e;
			}
		}
		fThreshold = (int) (fEntries.length * fLoadFactor);
	}
	
	@Override
	public V put(final int key, final V value) {
		for (Entry<V> e = fEntries[idxFor(key)]; e != null; e = e.next) {
			if (e.key == key) {
				final V old = e.value;
				e.value = value;
				return old;
			}
		}
		if (fSize >= fThreshold) {
			increase();
		}
		{	final int idx = idxFor(key);
			fEntries[idx] = new Entry<V>(key, value, fEntries[idx]);
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
		final int idx = idxFor(key);
		for (Entry<V> e = fEntries[idx], prev = null; e != null; prev = e, e = e.next) {
			if (e.key == key) {
				if (prev == null) {
					fEntries[idx] = e.next;
				}
				else {
					prev.next = e.next;
				}
				fSize--;
				final V oldValue = e.value;
				e.value = null;
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
		for (int i = fEntries.length-1; i >= 0; i--) {
			fEntries[i] = null;
		}
		fSize = 0;
	}
	
	
	public Set<IntEntry<V>> entryIntSet() {
		final Set<IntEntry<V>> entries = fEntryIntSet;
		return (entries != null) ? entries : (fEntryIntSet = new AbstractSet<IntEntry<V>>() {
			@Override
			public int size() {
				return fSize;
			}
			@Override
			public boolean contains(final Object o) {
				return (o instanceof IntEntry
						&& o.equals(IntHashMap.this.get(((IntEntry<?>) o).getIntKey())));
			}
			@Override
			public Iterator<IntEntry<V>> iterator() {
				return new IntEntryIterator();
			}
			@Override
			public void clear() {
				IntHashMap.this.clear();
			}
		});
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
		throw new UnsupportedAddressTypeException();
	}
	
}
