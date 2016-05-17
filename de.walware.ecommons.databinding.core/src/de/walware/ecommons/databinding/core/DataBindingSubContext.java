/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding.core;

import java.util.ArrayList;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;


public class DataBindingSubContext {
	
	
	private final DataBindingContext dbc;
	
	private final ArrayList<Binding> bindings;
	private final ArrayList<IObservable> observables;
	
	private final IChangeListener listener;
	
	private boolean enabled= true;
	
	
	public DataBindingSubContext(final DataBindingContext dbc, final IChangeListener listener) {
		this.dbc= dbc;
		this.bindings= new ArrayList<>(8);
		this.observables= new ArrayList<>(8);
		this.listener= listener;
	}
	
	public DataBindingSubContext(final DataBindingContext dbc) {
		this(dbc, null);
	}
	
	
	public DataBindingContext getDataBindingContext() {
		return this.dbc;
	}
	
	public void run(final Runnable runnable) {
		final IListChangeListener bindingsListener= new IListChangeListener() {
			@Override
			public void handleListChange(final ListChangeEvent event) {
				for (final ListDiffEntry diff : event.diff.getDifferences()) {
					if (diff.isAddition()) {
						addBinding((Binding) diff.getElement());
					}
					else {
						removeBinding((Binding) diff.getElement());
					}
				}
			}
		};
		
		this.dbc.getBindings().addListChangeListener(bindingsListener);
		try {
			final IObservable[] observables= ObservableTracker.runAndCollect(runnable);
			this.observables.ensureCapacity(this.observables.size() + observables.length);
			for (int i= 0; i < observables.length; i++) {
				this.observables.add(observables[i]);
			}
		}
		finally {
			this.dbc.getBindings().removeListChangeListener(bindingsListener);
		}
	}
	
	protected void addBinding(final Binding binding) {
		this.bindings.add(binding);
		if (this.listener != null) {
			binding.getTarget().addChangeListener(this.listener);
		}
	}
	
	protected void removeBinding(final Binding binding) {
		this.bindings.add(binding);
		if (this.listener != null) {
			binding.getTarget().addChangeListener(this.listener);
		}
	}
	
	public void setEnabled(final boolean enabled) {
		if (enabled == this.enabled) {
			return;
		}
		
		this.enabled= enabled;
		
		if (this.enabled) {
			for (final Binding binding : this.bindings) {
				this.dbc.addBinding(binding);
			}
		}
		else {
			for (final Binding binding : this.bindings) {
				this.dbc.removeBinding(binding);
			}
		}
	}
	
	public void dispose() {
		for (final Binding binding : this.bindings) {
			if (!binding.isDisposed()) {
				binding.dispose();
			}
		}
		this.bindings.clear();
		
		for (final IObservable observable : this.observables) {
			if (!observable.isDisposed()) {
				observable.dispose();
			}
		}
		this.observables.clear();
	}
	
}
