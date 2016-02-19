/*=============================================================================#
 # Copyright (c) 2014-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.collections;

import java.util.Collection;

import de.walware.jcommons.lang.Immutable;


/**
 * Immutable collection.
 * 
 * <p>The collection is unmodifiable by clients and, if not otherwise documented, the client can 
 * assume that the elements of the set do not change.
 * </p>
 */
public interface ImCollection<E> extends Collection<E>, Immutable {
	
	
}
