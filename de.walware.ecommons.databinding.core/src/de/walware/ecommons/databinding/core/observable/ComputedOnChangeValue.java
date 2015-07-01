/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding.core.observable;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.ComputedValue;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;


/**
 * Similar to {@link ComputedValue}, but using a fixed list of dependencies.
 */
public abstract class ComputedOnChangeValue extends AbstractObservableValue 
		implements IChangeListener {
	
	
	private final Object valueType;
	
	private final ImList<IObservable> dependencies;
	
	private boolean setting;
	
	private Object value;
	
	
	public ComputedOnChangeValue(final Object valueType, final IObservable... dependencies) {
		super(dependencies[0].getRealm());
		this.valueType= valueType;
		this.dependencies= ImCollections.newList(dependencies);
		for (final IObservable obs : dependencies) {
			obs.addChangeListener(this);
		}
	}
	
	@Override
	public synchronized void dispose() {
		for (final IObservable obs : this.dependencies) {
			obs.removeChangeListener(this);
		}
		super.dispose();
	}
	
	
	@Override
	public void handleChange(final ChangeEvent event) {
		if (!this.setting) {
			final Object newValue= calculate();
			final Object oldValue= this.value;
			if ((oldValue != null) ? !oldValue.equals(newValue) : null != newValue) {
				fireValueChange(Diffs.createValueDiff(oldValue, this.value= newValue));
			}
		}
	}
	
	@Override
	public Object getValueType() {
		return this.valueType;
	}
	
	@Override
	protected final Object doGetValue() {
		return calculate();
	}
	
	@Override
	protected final void doSetValue(final Object value) {
		this.setting= true;
		try {
			extractAndSet(value);
			this.value= value;
		}
		finally {
			this.setting= false;
		}
	}
	
	protected abstract Object calculate();
	
	protected void extractAndSet(final Object value) {
		throw new UnsupportedOperationException();
	}
	
}
