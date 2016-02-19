/*=============================================================================#
 # Copyright (c) 2013-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.graphics.core;


public class ColorRefDef extends ColorDef {
	
	
	private final ColorDef fRef;
	
	
	public ColorRefDef(final ColorDef ref) {
		super(ref);
		
		fRef = ref;
	}
	
	
	public final ColorDef getRef() {
		return fRef;
	}
	
}
