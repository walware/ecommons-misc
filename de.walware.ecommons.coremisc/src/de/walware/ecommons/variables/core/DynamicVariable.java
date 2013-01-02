/*******************************************************************************
 * Copyright (c) 2009-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.variables.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariable;


/**
 * Dynamic variable to provide easily variable resolving for a given string variable.
 */
public abstract class DynamicVariable extends StringVariable implements IDynamicVariable {
	
	
	public static abstract class LocationVariable extends DynamicVariable implements ILocationVariable {
		
		
		public LocationVariable(final IStringVariable variable) {
			super(variable);
		}
		
	}
	
	public static class ResolverVariable extends DynamicVariable {
		
		
		private final IDynamicVariableResolver fResolver;
		
		
		public ResolverVariable(final IStringVariable variable, final IDynamicVariableResolver resolver) {
			super(variable);
			
			fResolver = resolver;
		}
		
		
		public String getValue(final String argument) throws CoreException {
			return fResolver.resolveValue(this, argument);
		}
		
	}
	
	public static class StaticVariable extends DynamicVariable {
		
		
		private final String fValue;
		
		
		public StaticVariable(final IStringVariable variable, final String value) {
			super(variable);
			fValue = value;
		}
		
		
		@Override
		public boolean supportsArgument() {
			return false;
		}
		
		public String getValue(final String argument) throws CoreException {
			return fValue;
		}
		
		
	}
	
	public static class UnresolvedVariable extends DynamicVariable {
		
		
		private final CoreException fException;
		
		
		public UnresolvedVariable(final IStringVariable variable, final CoreException exception) {
			super(variable);
			fException = exception;
		}
		
		
		@Override
		public boolean supportsArgument() {
			return false;
		}
		
		public String getValue(final String argument) throws CoreException {
			throw fException;
		}
		
		
	}
	
	
	public DynamicVariable(final IStringVariable variable) {
		super(variable.getName(), variable.getDescription());
	}
	
	
	public boolean supportsArgument() {
		return false;
	}
	
}
