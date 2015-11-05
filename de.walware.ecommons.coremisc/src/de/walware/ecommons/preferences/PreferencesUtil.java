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

package de.walware.ecommons.preferences;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.ecommons.coreutils.internal.CoreMiscellanyPlugin;
import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.core.util.PreferenceUtils;


public final class PreferencesUtil {
	
	
	public final static IPreferenceAccess getInstancePrefs() {
		return PreferenceUtils.getInstancePrefs();
	}
	
	public final static IPreferenceAccess getDefaultPrefs() {
		return PreferenceUtils.getDefaultPrefs();
	}
	
	
	public static <T> T getPrefValue(final List<IScopeContext> contexts, final Preference<T> key) {
		return PreferenceUtils.getPrefValue(contexts, key);
	}
	
	public static <T> T getPrefValue(final IScopeContext context, final Preference<T> key) {
		return PreferenceUtils.getPrefValue(context, key);
	}
	
	public static <T> void setPrefValue(final IScopeContext context,
			final Preference<T> key, final T value) {
		PreferenceUtils.setPrefValue(context, key, value);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void setPrefValues(final IScopeContext context,
			final Map<Preference<?>, Object> preferencesMap) {
		PreferenceUtils.setPrefValues(context, preferencesMap);
	}
	
	public static <T> void setPrefValue(final IEclipsePreferences node,
			final Preference<T> key, final T value) {
		PreferenceUtils.setPrefValue(node, key, value);
	}
	
	
	/**
	 * Returns global instance of notifier service
	 * 
	 * @return the notifier
	 */
	public static SettingsChangeNotifier getSettingsChangeNotifier() {
		// Adapt this if used in other context
		final CoreMiscellanyPlugin plugin= CoreMiscellanyPlugin.getDefault();
		if (plugin != null) {
			return plugin.getSettingsChangeNotifier();
		}
		return null;
	}
	
}
