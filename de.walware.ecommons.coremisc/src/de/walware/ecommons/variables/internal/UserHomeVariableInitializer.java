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

package de.walware.ecommons.variables.internal;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class UserHomeVariableInitializer implements IValueVariableInitializer {
	
	
	public UserHomeVariableInitializer() {
	}
	
	
	@Override
	public void initialize(final IValueVariable variable) {
		variable.setValue(System.getProperty("user.home")); //$NON-NLS-1$
	}
	
}
