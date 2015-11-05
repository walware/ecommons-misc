/*=============================================================================#
 # Copyright (c) 2006-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.core;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;


/**
 * Interface to access preferences using <code>Preference</code>.
 * <p>
 * In most cases, you can take the Objects from <code>PreferencesUtil</code>.
 */
public interface IPreferenceAccess {
	
	
	/**
	 * Returns the scopes used by this agent.
	 * 
	 * @return array with all scopes used for lookup.
	 */
	ImList<IScopeContext> getPreferenceContexts();
	
	/**
	 * Returns the preference value of the specified <code>Preference</code>
	 * 
	 * @param <T> type for which the <code>Preference</code> is designed.
	 * @param pref
	 * @return value
	 */
	<T> T getPreferenceValue(Preference<T> pref);
	
	/**
	 * Register the given listener at the node in all scopes
	 * (which can changed).
	 * @see IEclipsePreferences#addPreferenceChangeListener(IPreferenceChangeListener)
	 * 
	 * @param nodeQualifier the qualifier of the node
	 * @param listener the listener
	 */
	void addPreferenceNodeListener(String nodeQualifier, IPreferenceChangeListener listener);
	
	/**
	 * Remove the given listener from the node in all scopes
	 * (registered with {@link #addPreferenceNodeListener(String, IPreferenceChangeListener)}).
	 * @see IEclipsePreferences#removePreferenceChangeListener(IPreferenceChangeListener)
	 * 
	 * @param nodeQualifier the qualifier of the node
	 * @param listener the listener
	 */
	void removePreferenceNodeListener(String nodeQualifier, IPreferenceChangeListener listener);
	
	/**
	 * Subscribes the given preference changeset listener for all scopes of this preference access
	 * and the specified preference node qualifiers.
	 * 
	 * @see IPreferenceSetService#addChangeListener(IPreferenceSetService.IChangeListener)
	 * 
	 * @param listener the listener
	 * @param qualifiers list of preference node qualifiers
	 */
	void addPreferenceSetListener(IPreferenceSetService.IChangeListener listener,
			ImSet<String> qualifiers);
	
	/**
	 * Unsubscribes the given preference changeset listener completely
	 * (registered with {@link #addPreferenceSetListener(IPreferenceSetService.IChangeListener)}).
	 * 
	 * @see IPreferenceSetService#removeChangeListener(IPreferenceSetService.IChangeListener)
	 * 
	 * @param listener the listener
	 */
	void removePreferenceSetListener(IPreferenceSetService.IChangeListener listener);
	
}
