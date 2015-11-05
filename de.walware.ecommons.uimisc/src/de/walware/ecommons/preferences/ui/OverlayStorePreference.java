/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.ui;

import de.walware.ecommons.preferences.core.Preference;


public class OverlayStorePreference {
	
	public static enum Type { 
			STRING, 
			BOOLEAN, 
			DOUBLE,
			FLOAT,
			LONG,
			INT;
	};
	
	
	public static OverlayStorePreference create(final Preference pref) {
		final Type type;
		if (pref instanceof Preference.BooleanPref) {
			type= Type.BOOLEAN;
		}
		else if (pref instanceof Preference.IntPref) {
			type= Type.INT;
		}
		else {
			type = Type.STRING;
		}
		return new OverlayStorePreference(pref.getKey(), type);
	}
	
	
	public final String fKey;
	public final Type fType;
	
	
	public OverlayStorePreference(final String key, final Type type) {
		fKey = key;
		fType = type;
	}
	
}
