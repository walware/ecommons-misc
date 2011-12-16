/*******************************************************************************
 * Copyright (c) 2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.io.win;

import org.eclipse.core.runtime.Platform;


public class DDE {
	
	
	static boolean gIsAvailable = Platform.getOS().equals(Platform.OS_WIN32);
	
	
	public static boolean isSupported() {
		return gIsAvailable;
	}
	
}
