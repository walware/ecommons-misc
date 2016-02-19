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

package de.walware.ecommons.text.core;

import org.eclipse.jface.text.IRegion;


public interface ITextRegion extends IRegion {
	
	
	/**
	 * Returns the offset the region begins.
	 *
	 * @return the beginning offset, inclusive.
	 */
	@Override
	int getOffset();
	
	/**
	 * Returns the offset the region ends.
	 *
	 * @return the ending offset, exclusive.
	 */
	int getEndOffset();
	
}
