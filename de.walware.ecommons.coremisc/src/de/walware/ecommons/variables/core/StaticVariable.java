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

package de.walware.ecommons.variables.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;


public class StaticVariable extends StringVariable implements IDynamicVariable {
	
	
	private final String value;
	
	
	public StaticVariable(final String name, final String description, final String value) {
		super(name, description);
		
		this.value= value;
	}
	
	public StaticVariable(final IStringVariable variable, final String value) {
		this(variable.getName(), variable.getDescription(), value);
	}
	
	
	@Override
	public boolean supportsArgument() {
		return false;
	}
	
	@Override
	public String getValue(final String argument) throws CoreException {
		return this.value;
	}
	
}
