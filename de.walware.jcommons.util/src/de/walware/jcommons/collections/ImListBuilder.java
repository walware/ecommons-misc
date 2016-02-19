/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.collections;

import java.util.ArrayList;

import de.walware.jcommons.lang.Builder;


public class ImListBuilder<E> extends ArrayList<E> implements Builder<ImList<E>> {
	
	private static final long serialVersionUID= 1L;
	
	
	/**
	 * Constructs an empty list with the specified initial capacity.
	 *
	 * @param  initialCapacity  the initial capacity of the list
	 */
	public ImListBuilder(final int initialCapacity) {
		super(initialCapacity);
	}
	
	public ImListBuilder() {
		super();
	}
	
	
	@Override
	public ImList<E> build() {
		return ImCollections.toList(this);
	}
	
}
