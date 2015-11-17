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

package de.walware.jcommons.collections;

import java.util.List;


/**
 * Immutable list.
 * 
 * <p>The list is unmodifiable by clients and, if not otherwise documented, the client can assume
 * that the elements of the list do not change.
 * </p>
 */
public interface ImList<E> extends List<E>, ImCollection<E> {
	
	
	@Override
	ImList<E> subList(int fromIndex, int toIndex);
	
}
