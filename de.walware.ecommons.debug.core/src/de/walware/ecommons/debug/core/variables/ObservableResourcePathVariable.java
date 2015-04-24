/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.variables;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.variables.IStringVariable;

import de.walware.ecommons.variables.core.ObservableValueVariable;


public class ObservableResourcePathVariable extends ObservableValueVariable {
	
	
	public ObservableResourcePathVariable(final String name, final String description,
			final IObservableValue observable) {
		super(name, description, observable);
	}
	
	public ObservableResourcePathVariable(final IStringVariable variable,
			final IObservableValue observable) {
		super(variable, observable);
	}
	
	
	@Override
	protected String toVariableValue(final Object value) {
		return (value != null) ? ((IResource) value).getFullPath().toString() : null;
	}
	
}
