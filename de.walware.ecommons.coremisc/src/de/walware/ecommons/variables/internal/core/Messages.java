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

package de.walware.ecommons.variables.internal.core;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String Validation_Syntax_DollorInvalidChar_message;
	public static String Validation_Syntax_DollarEnd_message;
	public static String Validation_Syntax_VarMissingName_message;
	public static String Validation_Syntax_VarInvalidChar_message;
	public static String Validation_Syntax_VarNotClosed_message;
	public static String Validation_Ref_VarNotDefined_message;
	public static String Validation_Ref_VarNoArgs_message;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
