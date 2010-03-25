/*******************************************************************************
 * Copyright (c) 2009-2010 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons;


public class IntArrayMap<E extends Object> {
	
	
	private Object[] fArray;
	private int fLength;
	
	
	public IntArrayMap() {
		fArray = new Object[16];
	}
	
	public IntArrayMap(final int initialCapacity) {
		fArray = new Object[initialCapacity];
	}
	
	
	private void increase(final int min) {
		final Object[] newArray = new Object[min+16];
		System.arraycopy(fArray, 0, newArray, 0, fLength);
		fArray = newArray;
	}
	
	public void put(final int key, final E value) {
		if (key >= fLength) {
			if (key >= fArray.length) {
				increase(key);
			}
			fLength = key + 1;
		}
		fArray[key] = value;
	}
	
	public E get(final int key) {
		if (key < fLength) {
			return (E) fArray[key];
		}
		return null;
	}
	
	public E getAndPut(final int key, final E value) {
		if (key >= fLength) {
			if (key >= fArray.length) {
				increase(key);
			}
			fLength = key + 1;
		}
		final E prev = (E) fArray[key];
		fArray[key] = value;
		return prev;
	}
	
	public int size() {
		return fLength;
	}
	
	public E[] toArray(final E[] array) {
		System.arraycopy(fArray, 0, array, 0, fLength);
		return array;
	}
	
}
