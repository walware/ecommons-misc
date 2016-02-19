/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.collections;

import java.util.HashMap;
import java.util.Map;


/**
 * Map with String keys using case insensitive comparison.
 * <p>
 * For example for environment variables on windows.
 */
public class CaseInsensitiveMap<V> extends HashMap<String, V> {
	
	
	private static final long serialVersionUID= 1L;
	
	
	// id = upper case key
	// name = case sensitive key
	
	private final Map<String, String> idNameMap;
	
	
	public CaseInsensitiveMap() {
		this.idNameMap= new HashMap<>();
	}
	
	public CaseInsensitiveMap(final int initialCapacity) {
		super(initialCapacity);
		this.idNameMap= new HashMap<>(initialCapacity);
	}
	
	
	@Override
	public V put(final String name, final V value) {
		if (name == null) {
			throw new NullPointerException();
		}
		final String id= name.toUpperCase();
		final String oldName= this.idNameMap.put(id, name);
		V prevValue;
		if (!name.equals(oldName)) {
			prevValue= super.remove(oldName);
			super.put(name, value);
		}
		else {
			prevValue= super.put(name, value);
		}
		return prevValue;
	}
	
	@Override
	public void putAll(final Map<? extends String, ? extends V> t) {
		for (final Map.Entry<? extends String, ? extends V> entry : t.entrySet()) {
			put(entry.getKey(), entry.getValue());
		}
	}
	
	@Override
	public V remove(final Object key) {
		if (!(key instanceof String)) {
			return null;
		}
		final String id= ((String) key).toUpperCase();
		final String realName= this.idNameMap.remove(id);
		if (realName == null) {
			return null;
		}
		return super.remove(realName);
	}
	
	@Override
	public void clear() {
		this.idNameMap.clear();
		super.clear();
	}
	
	
	@Override
	public boolean isEmpty() {
		return this.idNameMap.isEmpty();
	}
	
	@Override
	public int size() {
		return this.idNameMap.size();
	}
	
	@Override
	public boolean containsKey(final Object key) {
		if (!(key instanceof String)) {
			return false;
		}
		final String id= ((String) key).toUpperCase();
		return this.idNameMap.containsKey(id);
	}
	
//	@Override
//	public boolean containsValue(final Object value) {
//		return super.containsValue(value);
//	}
	
	@Override
	public V get(final Object key) {
		if (!(key instanceof String)) {
			return null;
		}
		final String id= ((String) key).toUpperCase();
		final String name= this.idNameMap.get(id);
		return (name != null) ? super.get(name) : null;
	}
	
	
//	@Override
//	public Set<Entry<String, String>> entrySet() {
//		return super.entrySet();
//	}
	
//	@Override
//	public Set<String> keySet() {
//		return super.keySet();
//	}
	
//	@Override
//	public Collection<String> values() {
//		return super.values();
//	}
	
}
