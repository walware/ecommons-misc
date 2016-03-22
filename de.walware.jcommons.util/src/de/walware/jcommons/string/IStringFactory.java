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

package de.walware.jcommons.string;


/**
 * Implement to provide a strategy to create (intern, compact) or share string instances.
 */
public interface IStringFactory {
	
	
	String get(CharArrayString s);
	
	String get(CharSequence s);
	
	String get(String s, boolean isCompact);
	
	
}
