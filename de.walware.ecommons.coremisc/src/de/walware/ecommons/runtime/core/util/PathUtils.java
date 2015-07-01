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

package de.walware.ecommons.runtime.core.util;

import org.eclipse.core.runtime.IPath;


public class PathUtils {
	
	
	public static boolean isValid(final IPath path) {
		for (int i= 0; i < path.segmentCount(); i++) {
			if (!path.isValidSegment(path.segment(i))) {
				return false;
			}
		}
		return true;
	}
	
	public static IPath check(final IPath path) {
		return (path != null && isValid(path)) ? path : null;
	}
	
}
