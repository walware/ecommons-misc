/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.ui;

import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.graphics.RGB;

import de.walware.ecommons.preferences.core.Preference;


/**
 * Preference of a color value as RGB (like in JFace).
 */
public class RGBPref extends Preference<RGB> {
	
	
	public RGBPref(final String qualifier, final String key) {
		super(qualifier, key);
	}
	
	
	@Override
	public Class<RGB> getUsageType() {
		return RGB.class;
	}
	
	@Override
	public RGB store2Usage(final String storeValue) {
		if (storeValue != null) {
			return StringConverter.asRGB(storeValue);
		}
		return null;
	}
	
	@Override
	public String usage2Store(final RGB value) {
		return StringConverter.asString(value);
	}
	
}
