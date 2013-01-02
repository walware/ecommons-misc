/*******************************************************************************
 * Copyright (c) 2012-2013 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.variables.internal;

import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.IValueVariableInitializer;

public class UserHomeVariableInitializer implements IValueVariableInitializer {
	
	
	public UserHomeVariableInitializer() {
	}
	
	
	public void initialize(IValueVariable variable) {
		variable.setValue(System.getProperty("user.home")); //$NON-NLS-1$
	}
	
}
