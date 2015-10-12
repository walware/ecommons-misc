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

package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.ISynchronizable;


public final class TextUtils {
	
	
	public static Object getLockObject(final Object o) {
		final Object lockObject= (o instanceof ISynchronizable) ?
				((ISynchronizable) o).getLockObject() : null;
		return (lockObject != null) ? lockObject : o;
	}
	
	
}
