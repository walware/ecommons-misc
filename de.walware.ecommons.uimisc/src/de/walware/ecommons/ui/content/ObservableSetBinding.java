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

package de.walware.ecommons.ui.content;

import java.util.Collection;

import org.eclipse.core.databinding.observable.set.IObservableSet;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;

import de.walware.jcommons.collections.ImCollections;


public class ObservableSetBinding implements ISetChangeListener {
	
	
	private final TableFilterController controller;
	
	private final SetElementFilter filter;
	
	
	public ObservableSetBinding(final TableFilterController controller, final IObservableSet set, final SetElementFilter filter) {
		this.controller= controller;
		this.filter= filter;
		set.addSetChangeListener(this);
	}
	
	
	protected Collection<?> getAll() {
		return null;
	}
	
	protected Collection<?> createFilterSet(final Collection<?> set) {
		return ImCollections.toList(set);
	}
	
	@Override
	public void handleSetChange(final SetChangeEvent event) {
		final IObservableSet set= event.getObservableSet();
		final Collection<?> copy;
		final Collection<?> all;
		if (set.isEmpty() || ((all= getAll()) != null && set.containsAll(all))) {
			copy= null;
		}
		else {
			copy= createFilterSet(set);
		}
		if (this.filter.setSet(copy)) {
			this.controller.refresh(true);
		}
	}
	
}
