/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.collections;

import java.util.Map;


/**
 * An object that maps integer keys to values. This interface and its implementations provide
 * methods with primitive integer arguments.
 * 
 * @param <V> type of the values
 * @since 1.0
 */
public interface IntMap<V> extends Map<Integer, V> {
	
	interface IntEntry<V> {
		
		int getIntKey();
		V getValue();
		
	}
	
	boolean containsKey(int key);
	V get(int key);
	
	V put(int key, V value);
	
	/*Set<IntEntry<V>> entryIntSet();*/
	
}
