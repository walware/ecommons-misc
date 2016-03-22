/*=============================================================================#
 # Copyright (c) 2008-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ts.util;

import java.util.Map;


/**
 * Utilities for implementations of {@link de.walware.ecommons.ts.IToolCommandHandler}.
 */
public class ToolCommandHandlerUtil {
	
	
	@SuppressWarnings("unchecked")
	public static <C> C getCheckedData(final Map<String, Object> data, final String name, final Class<C> clazz, final boolean required) {
		final Object obj = data.get(name);
		if (obj == null) {
			if (required) {
				throw new IllegalArgumentException("missing data entry: '" + name + '"');
			}
			return null;
		}
		if (!clazz.isInstance(obj)) {
			throw new IllegalArgumentException("incompatible data entry: '" + name + "' (" + obj.getClass().getName() + ")");
		}
		return (C) obj;
	}
	
	@SuppressWarnings("unchecked")
	public static <C> C getCheckedData(final Map<String, Object> data, final String name, final C defValue) {
		final Object obj = data.get(name);
		if (obj == null) {
			return defValue;
		}
		if (!defValue.getClass().isInstance(obj)) {
			throw new IllegalArgumentException("incompatible data entry: '" + name + "' (" + obj.getClass().getName() + ")");
		}
		return (C) obj;
	}
	
	
	private ToolCommandHandlerUtil() {}
	
}
