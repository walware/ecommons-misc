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

import java.util.Collection;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IValueVariable;


public class VariableUtils {
	
	
	public static String getValue(final IStringVariable variable) throws CoreException {
		if (variable instanceof IValueVariable) {
			return ((IValueVariable) variable).getValue();
		}
		else /*(variable instanceof IDynamicVariable)*/ {
			return ((IDynamicVariable) variable).getValue(null);
		}
	}
	
	
	public static <T extends IStringVariable> void add(final Map<String, ? super T> variables,
			final T variableToAdd) {
		variables.put(variableToAdd.getName(), variableToAdd);
	}
	
	public static <T extends IStringVariable> void add(final Map<String, ? super T> variables,
			final Collection<T> variablesToAdd) {
		for (final T variable : variablesToAdd) {
			variables.put(variable.getName(), variable);
		}
	}
	
	public static void add(final Map<String, ? super IDynamicVariable> variables,
			final Collection<IDynamicVariable> variablesToAdd, final IDynamicVariableResolver resolver) {
		for (final IDynamicVariable variable : variablesToAdd) {
			variables.put(variable.getName(),
					new DynamicVariable.ResolverVariable(variable, resolver) );
		}
	}
	
	
	public static IDynamicVariable toStaticVariable(final IStringVariable variable) {
		return toStaticVariable(variable, variable);
	}
	
	public static IDynamicVariable toStaticVariable(final IStringVariable defVariable,
			final IStringVariable valueVariable) {
		try {
			final String value= getValue(valueVariable);
			return new StaticVariable(defVariable, value);
		}
		catch (final CoreException exception) {
			return new UnresolvedVariable(defVariable, exception);
		}
	}
	
}
