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
import org.eclipse.core.variables.IStringVariable;

public class UnresolvedVariable extends DynamicVariable {
	
	
	private final CoreException exception;
	
	
	public UnresolvedVariable(final IStringVariable variable,
			final CoreException exception) {
		super(variable);
		
		this.exception= exception;
	}
	
	
	@Override
	public String getValue(final String argument) throws CoreException {
		throw this.exception;
	}
	
}
