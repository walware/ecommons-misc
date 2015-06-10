/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.string;


public final class InternStringFactory implements IStringFactory {
	
	
	public static final IStringFactory INSTANCE = new InternStringFactory();
	
	
	public InternStringFactory() {
	}
	
	
	@Override
	public String get(final CharArrayString s) {
		return s.toString().intern();
	}
	
	@Override
	public String get(final CharSequence s) {
		return s.toString().intern();
	}
	
	@Override
	public String get(final String s, final boolean isCompact) {
		return s.intern();
	}
	
}
