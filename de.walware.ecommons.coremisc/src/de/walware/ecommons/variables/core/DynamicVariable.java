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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariable;


/**
 * Dynamic variable to provide easily variable resolving for a given string variable.
 */
public class DynamicVariable extends StringVariable implements IDynamicVariable {
	
	
	@Deprecated
	public static abstract class LocationVariable extends DynamicVariable implements ILocationVariable {
		
		
		public LocationVariable(final IStringVariable variable) {
			super(variable);
		}
		
	}
	
	public static class ResolverVariable extends DynamicVariable {
		
		
		private final IDynamicVariableResolver resolver;
		
		
		public ResolverVariable(final String name, final String description,
				final boolean supportsArgument, final IDynamicVariableResolver resolver) {
			super(name, description, supportsArgument);
			
			if (resolver == null) {
				throw new NullPointerException("resolver"); //$NON-NLS-1$
			}
			
			this.resolver= resolver;
		}
		
		public ResolverVariable(final IStringVariable variable,
				final IDynamicVariableResolver resolver) {
			super(variable);
			
			if (resolver == null) {
				throw new NullPointerException("resolver"); //$NON-NLS-1$
			}
			
			this.resolver= resolver;
		}
		
		
		@Override
		public String getValue(final String argument) throws CoreException {
			return this.resolver.resolveValue(this, argument);
		}
		
	}
	
	
	private final boolean isArgumentSupported;
	
	
	public DynamicVariable(final String name, final String description, final boolean
			supportsArgument) {
		super(name, description);
		
		this.isArgumentSupported= supportsArgument;
	}
	
	public DynamicVariable(final IStringVariable variable) {
		super(variable.getName(), variable.getDescription());
		
		this.isArgumentSupported= (variable instanceof IDynamicVariable
				&& ((IDynamicVariable) variable).supportsArgument() );
	}
	
	
	@Override
	public boolean supportsArgument() {
		return this.isArgumentSupported;
	}
	
	@Override
	public String getValue(final String argument) throws CoreException {
		throw new CoreException(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
				"At the moment not resolvable." ));
	}
	
}
