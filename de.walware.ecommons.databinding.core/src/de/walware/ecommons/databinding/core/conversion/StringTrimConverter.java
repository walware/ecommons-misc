/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding.core.conversion;

import org.eclipse.core.databinding.conversion.IConverter;


public class StringTrimConverter implements IConverter {
	
	
	public static final IConverter INSTANCE= new StringTrimConverter();
	
	
	public StringTrimConverter() {
	}
	
	
	@Override
	public Object getFromType() {
		return String.class;
	}
	
	@Override
	public Object getToType() {
		return String.class;
	}
	
	@Override
	public Object convert(final Object fromObject) {
		return ((String) fromObject).trim();
	}
	
}
