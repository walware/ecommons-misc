/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.lang;


public class SystemUtils {
	
	
	public static final String OS_NAME_KEY= "os.name"; //$NON-NLS-1$
	public static final String OS_ARCH_KEY= "os.arch"; //$NON-NLS-1$
	public static final String OS_VERSION_KEY= "os.version"; //$NON-NLS-1$
	
	public static final String FILE_SEPARATOR_KEY= "file.separator"; //$NON-NLS-1$
	public static final String PATH_SEPARATOR_KEY= "path.separator"; //$NON-NLS-1$
	public static final String LINE_SEPARATOR_KEY= "line.separator"; //$NON-NLS-1$
	
	public static final String USER_HOME_KEY= "user.home"; //$NON-NLS-1$
	
	
	public static boolean isOSWindows(final String osName) {
		return (osName.startsWith("Windows", 0)); //$NON-NLS-1$
	}
	
	public static boolean isOSMac(final String osName) {
		return (osName.startsWith("Mac OS", 0)); //$NON-NLS-1$
	}
	
}
