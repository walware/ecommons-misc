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

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IStringVariable;


public class ObservableValueVariable extends DynamicVariable implements IObservableVariable {
	
	
	private final IObservableValue observable;
	
	
	public ObservableValueVariable(final String name, final String description,
			final IObservableValue observable) {
		super(name, description, false);
		
		if (observable == null) {
			throw new NullPointerException("observable"); //$NON-NLS-1$
		}
		
		this.observable= observable;
	}
	
	public ObservableValueVariable(final IStringVariable variable,
			final IObservableValue observable) {
		this(variable.getName(), variable.getDescription(), observable);
	}
	
	
	public IObservableValue getObservable() {
		return this.observable;
	}
	
	@Override
	public void addChangeListener(final IChangeListener listener) {
		this.observable.addChangeListener(listener);
	}
	
	@Override
	public void removeChangeListener(final IChangeListener listener) {
		this.observable.removeChangeListener(listener);
	}
	
	
	@Override
	public String getValue(final String argument) throws CoreException {
		final String value= toVariableValue(this.observable.getValue());
		if (value == null) {
			return super.getValue(null);
		}
		return value.toString();
	}
	
	protected String toVariableValue(final Object value) {
		return (value != null) ? value.toString() : null;
	}
	
}
