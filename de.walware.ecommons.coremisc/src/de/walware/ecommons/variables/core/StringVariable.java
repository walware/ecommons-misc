/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.variables.core;

import org.eclipse.core.variables.IStringVariable;


/**
 * Simple string value e.g. as additional entry in variable selection dialogs.
 */
public class StringVariable implements IStringVariable {
	
	
	private final String name;
	private final String description;
	
	
	/**
	 * Create a new variable.
	 * 
	 * @param name the name of the variable within <code>${}</code>
	 * @param description
	 */
	public StringVariable(final String name, final String description) {
		this.name= name;
		this.description= description;
	}
	
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getName() {
		return this.name;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getDescription() {
		return this.description;
	}
	
}
