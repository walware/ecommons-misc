/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.internal.core;

import org.eclipse.osgi.util.NLS;


public class Messages extends NLS {
	
	
	public static String LaunchDelegate_LaunchingTask_label;
	public static String LaunchDelegate_Init_subtask;
	
	public static String ResourceVariable_error_Resource_EmptySelection_message;
	public static String ResourceVariable_error_Resource_InvalidPath_message;
	public static String ResourceVariable_error_Resource_NonExisting_message;
	public static String ResourceVariable_error_Resource_LocationFailed_message;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
