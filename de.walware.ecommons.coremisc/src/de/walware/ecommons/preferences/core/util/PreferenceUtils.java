/*=============================================================================#
 # Copyright (c) 2006-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.core.util;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;

import de.walware.ecommons.coreutils.internal.CoreMiscellanyPlugin;
import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.Preference;


public final class PreferenceUtils {
	
	
	private static class DefaultImpl implements IPreferenceAccess {
		
		private final ImList<IScopeContext> contexts;
		
		
		private DefaultImpl(final ImList<IScopeContext> contexts) {
			this.contexts= contexts;
		}
		
		
		@Override
		public ImList<IScopeContext> getPreferenceContexts() {
			return this.contexts;
		}
		
		@Override
		public <T> T getPreferenceValue(final Preference<T> pref) {
			return PreferenceUtils.getPrefValue(this.contexts, pref);
		}
		
		@Override
		public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
			for (int i= 0; i < this.contexts.size(); i++) {
				final IScopeContext context= this.contexts.get(i);
				if (context instanceof DefaultScope) {
					continue;
				}
				final IEclipsePreferences node= context.getNode(nodeQualifier);
				if (node != null) {
					node.addPreferenceChangeListener(listener);
				}
			}
		}
		
		@Override
		public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
			for (int i= 0; i < this.contexts.size(); i++) {
				final IScopeContext context= this.contexts.get(i);
				if (context instanceof DefaultScope) {
					continue;
				}
				final IEclipsePreferences node= context.getNode(nodeQualifier);
				if (node != null) {
					node.removePreferenceChangeListener(listener);
				}
			}
		}
		
		@Override
		public void addPreferenceSetListener(final IPreferenceSetService.IChangeListener listener,
				final ImSet<String> qualifiers) {
			getPreferenceSetService().addChangeListener(listener,
					getPreferenceContexts(), qualifiers );
		}
		
		@Override
		public void removePreferenceSetListener(final IPreferenceSetService.IChangeListener listener) {
			final IPreferenceSetService service= getPreferenceSetService();
			if (service != null) {
				service.removeChangeListener(listener);
			}
		}
		
	}
	
	private static class MapImpl implements IPreferenceAccess {
		
		
		private final Map<Preference<?>, Object> preferencesMap;
		
		MapImpl(final Map<Preference<?>, Object> preferencesMap) {
			this.preferencesMap= preferencesMap;
		}
		
		@Override
		public ImList<IScopeContext> getPreferenceContexts() {
			return ImCollections.emptyList();
		}
		
		@Override
		@SuppressWarnings("unchecked")
		public <T> T getPreferenceValue(final Preference<T> pref) {
			return (T) this.preferencesMap.get(pref);
		}
		
		/**
		 * Not (yet) supported
		 */
		@Override
		public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Not (yet) supported
		 */
		@Override
		public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Not (yet) supported
		 */
		@Override
		public void addPreferenceSetListener(final IPreferenceSetService.IChangeListener listener,
				final ImSet<String> qualifiers) {
			throw new UnsupportedOperationException();
		}
		
		/**
		 * Not (yet) supported
		 */
		@Override
		public void removePreferenceSetListener(final IPreferenceSetService.IChangeListener listener) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	
	private final static DefaultImpl DEFAULT_PREFS= new DefaultImpl(ImCollections.newList(
			DefaultScope.INSTANCE ));
	
	private final static DefaultImpl INSTANCE_PREFS= new DefaultImpl(ImCollections.newList(
			InstanceScope.INSTANCE,
			DefaultScope.INSTANCE ));
	
	
	public final static IPreferenceAccess getInstancePrefs() {
		return INSTANCE_PREFS;
	}
	
	public final static IPreferenceAccess getDefaultPrefs() {
		return DEFAULT_PREFS;
	}
	
	
	public static IPreferenceAccess createAccess(final Map<Preference<?>, Object> preferencesMap) {
		return new MapImpl(preferencesMap);
	}
	
	public static IPreferenceAccess createAccess(final List<IScopeContext> contexts) {
		return new DefaultImpl(ImCollections.toList(contexts));
	}
	
	
	public static <T> T getPrefValue(final List<IScopeContext> contexts, final Preference<T> pref) {
		String storeValue= null;
		for (int i= 0; i < contexts.size() && storeValue == null; i++) {
			try {
				storeValue= contexts.get(i).getNode(pref.getQualifier()).get(pref.getKey(), null);
			}
			catch (final IllegalStateException e) {
			}
		}
		return pref.store2Usage(storeValue);
	}
	
	public static <T> T getPrefValue(final IScopeContext context, final Preference<T> pref) {
		final IEclipsePreferences node= context.getNode(pref.getQualifier());
		return getPrefValue(node, pref);
	}
	
	public static <T> T getPrefValue(final IEclipsePreferences node, final Preference<T> pref) {
		final String storeValue= node.get(pref.getKey(), (String) null);
		return pref.store2Usage(storeValue);
	}
	
	public static <T> void setPrefValue(final IScopeContext context,
			final Preference<T> pref, final T value) {
		final IEclipsePreferences node= context.getNode(pref.getQualifier());
		setPrefValue(node, pref, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setPrefValues(final IScopeContext context,
			final Map<Preference<?>, Object> preferencesMap) {
		for (final Map.Entry<Preference<?>, Object> pref : preferencesMap.entrySet()) {
			setPrefValue(context, (Preference) pref.getKey(), pref.getValue());
		}
	}
	
	public static <T> void setPrefValue(final IEclipsePreferences node,
			final Preference<T> pref, final T value) {
		final String storeValue;
		if (value == null
				|| (storeValue= pref.usage2Store(value)) == null) {
			node.remove(pref.getKey());
			return;
		}
		node.put(pref.getKey(), storeValue);
	}
	
	
	public static IPreferenceSetService getPreferenceSetService() {
		// Adapt this if used in other context
		final CoreMiscellanyPlugin plugin= CoreMiscellanyPlugin.getDefault();
		if (plugin != null) {
			return plugin.getPreferenceSetService();
		}
		return null;
	}
	
}
