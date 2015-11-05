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

package de.walware.ecommons.preferences.core.util;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;

import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.IPreferenceSetService.IChangeListener;
import de.walware.ecommons.preferences.core.Preference;


public class PreferenceAccessWrapper implements IPreferenceAccess {
	
	
	private static class ListenerItem {
		
		private final IPreferenceSetService.IChangeListener listener;
		
		private ImSet<String> qualifiers;
		
		
		public ListenerItem(final IPreferenceSetService.IChangeListener listener,
				final ImSet<String> qualifiers) {
			this.listener= listener;
			this.qualifiers= qualifiers;
		}
		
	}
	
	
	private volatile boolean isDisposed;
	
	private volatile ImList<IScopeContext> contexts;
	
	private final List<ListenerItem> listeners= new ArrayList<>();
	
	
	public PreferenceAccessWrapper(final ImList<IScopeContext> contexts) {
		this.contexts= contexts;
	}
	
	public PreferenceAccessWrapper() {
		this.contexts= ImCollections.emptyList();
	}
	
	
	public synchronized void setPreferenceContexts(final ImList<IScopeContext> contexts) {
		if (isDisposed() || this.contexts.equals(contexts)) {
			return;
		}
		
		this.contexts= contexts;
		
		final IPreferenceSetService service= PreferenceUtils.getPreferenceSetService();
		if (service != null) {
			synchronized (this.listeners) {
				final int l= this.listeners.size();
				for (int i= 0; i < l; i++) {
					final ListenerItem item= this.listeners.get(i);
					service.addChangeListener(item.listener,
							contexts, item.qualifiers );
				}
			}
		}
	}
	
	public synchronized void dispose() {
		if (isDisposed()) {
			return;
		}
		
		this.isDisposed= true;
		
		this.contexts= ImCollections.emptyList();
		
		final IPreferenceSetService service= PreferenceUtils.getPreferenceSetService();
		if (service != null) {
			synchronized (this.listeners) {
				final int l= this.listeners.size();
				for (int i= 0; i < l; i++) {
					final ListenerItem item= this.listeners.get(i);
					service.removeChangeListener(item.listener);
				}
				this.listeners.clear();
			}
		}
	}
	
	public final boolean isDisposed() {
		return this.isDisposed;
	}
	
	
	@Override
	public final ImList<IScopeContext> getPreferenceContexts() {
		return this.contexts;
	}
	
	@Override
	public final <T> T getPreferenceValue(final Preference<T> pref) {
		return PreferenceUtils.getPrefValue(this.contexts, pref);
	}
	
	@Override
	public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void addPreferenceSetListener(final IChangeListener listener, final ImSet<String> qualifiers) {
		if (listener == null) {
			throw new NullPointerException("listener"); //$NON-NLS-1$
		}
		if (qualifiers == null) {
			throw new NullPointerException("qualifiers"); //$NON-NLS-1$
		}
		
		LISTENER: synchronized (this.listeners) {
			if (isDisposed()) {
				return;
			}
			
			final int l= this.listeners.size();
			for (int i= 0; i < l; i++) {
				final ListenerItem item= this.listeners.get(i);
				if (item.listener == listener) {
					if (item.qualifiers.equals(qualifiers)) {
						return;
					}
					item.qualifiers= qualifiers;
					break LISTENER;
				}
			}
			this.listeners.add(new ListenerItem(listener, qualifiers));
		}
		
		final IPreferenceSetService service= PreferenceUtils.getPreferenceSetService();
		if (service != null) {
			synchronized (this) {
				if (isDisposed()) {
					return;
				}
				
				service.addChangeListener(listener,
						this.contexts, qualifiers );
			}
		}
	}
	
	@Override
	public void removePreferenceSetListener(final IChangeListener listener) {
		LISTENER: synchronized (this.listeners) {
			if (isDisposed()) {
				return;
			}
			
			final int l= this.listeners.size();
			for (int i= 0; i < l; i++) {
				final ListenerItem item= this.listeners.get(i);
				if (item.listener == listener) {
					this.listeners.remove(i);
					break LISTENER;
				}
			}
			return;
		}
		
		final IPreferenceSetService service= PreferenceUtils.getPreferenceSetService();
		if (service != null) {
			service.removeChangeListener(listener);
		}
	}
	
}
