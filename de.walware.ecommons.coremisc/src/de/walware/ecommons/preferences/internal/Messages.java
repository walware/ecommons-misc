/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.internal;

import org.eclipse.osgi.util.NLS;


public class Messages {
	
	
	public static String SettingsChangeNotifier_Job_title;
	public static String SettingsChangeNotifier_Task_name;
	
	
	static {
		NLS.initializeMessages(Messages.class.getName(), Messages.class);
	}
	private Messages() {}
	
}
