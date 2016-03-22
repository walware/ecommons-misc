/*=============================================================================#
 # Copyright (c) 2006-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ts;


/**
 * The service interface of the tool which can be used by the runnables.
 */
public interface IToolService {
	
	
	/**
	 * Returns the tool the service belong to
	 * 
	 * @return the tool handler
	 */
	ITool getTool();
	
}
