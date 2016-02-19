/*=============================================================================#
 # Copyright (c) 2011-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.string;


public final class StringFactory implements IStringFactory {
	
	
	public static final StringFactory INSTANCE = new StringFactory();
	
	
	public StringFactory() {
	}
	
	
	@Override
	public String get(final CharSequence s) {
		return s.toString();
	}
	
	@Override
	public String get(final CharArrayString s) {
		return s.toString();
	}
	
	@Override
	public String get(final String s, final boolean isCompact) {
		return (isCompact) ? s : new String(s);
	}
	
}
