/*=============================================================================#
 # Copyright (c) 2012-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding;

import java.util.ArrayList;

import org.eclipse.core.databinding.Binding;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.observable.IObservable;
import org.eclipse.core.databinding.observable.ObservableTracker;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;


public class DataBindingSubContext {
	
	
	private final DataBindingContext fDbc;
	
	private final ArrayList<Binding> fBindings;
	private final ArrayList<IObservable> fObservables;
	
	
	public DataBindingSubContext(final DataBindingContext dbc) {
		fDbc = dbc;
		fBindings = new ArrayList<Binding>(8);
		fObservables = new ArrayList<IObservable>(8);
	}
	
	
	public DataBindingContext getDataBindingContext() {
		return fDbc;
	}
	
	public void run(final Runnable runnable) {
		final IListChangeListener listener = new IListChangeListener() {
			@Override
			public void handleListChange(final ListChangeEvent event) {
				for (final ListDiffEntry diff : event.diff.getDifferences()) {
					if (diff.isAddition()) {
						fBindings.add((Binding) diff.getElement());
					}
				}
			}
		};
		
		fDbc.getBindings().addListChangeListener(listener);
		try {
			final IObservable[] observables = ObservableTracker.runAndCollect(runnable);
			fObservables.ensureCapacity(fObservables.size() + observables.length);
			for (int i = 0; i < observables.length; i++) {
				fObservables.add(observables[i]);
			}
		}
		finally {
			fDbc.getBindings().removeListChangeListener(listener);
		}
	}
	
	public void dispose() {
		for (final Binding binding : fBindings) {
			if (!binding.isDisposed()) {
				binding.dispose();
			}
		}
		fBindings.clear();
		
		for (final IObservable observable : fObservables) {
			if (!observable.isDisposed()) {
				observable.dispose();
			}
		}
		fObservables.clear();
	}
	
}
