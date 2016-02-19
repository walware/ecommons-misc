/*=============================================================================#
 # Copyright (c) 2004-2016 TeXlipse Project (texlipse.sf.net) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Tor Arne Vestbø - initial API and implementation
 #     Stephan Wahlbrink - adapted to ECommons and OSGi
 #=============================================================================*/

package de.walware.ecommons.io.win;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.coreutils.internal.CoreMiscellanyPlugin;


/** 
 * Small wrapper for Win32 DDE execute commands
 */
public class DDEClient {
	
	static {
		try {
			System.loadLibrary("ddeclient"); //$NON-NLS-1$
		}
		catch (final UnsatisfiedLinkError e) {
			if (DDE.isSupported()) {
				CoreMiscellanyPlugin.getDefault().log(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
						-1, "The DDEclient library could not be loaded.", e ));
			}
			DDE.gIsAvailable = false;
		}
	}
	
	
	public static final int INIT_FAILED= 1;
	public static final int CONNECT_FAILED= 2;
	
	
	public static void execute(final String server, final String topic, final String command)
			throws CoreException {
		if (server == null) {
			throw new NullPointerException("server");
		}
		if (topic == null) {
			throw new NullPointerException("topic");
		}
		if (command == null) {
			throw new NullPointerException("command");
		}
		final int error;
		if (DDE.isSupported()) {
			error = ddeExecute(server, topic, command);
		}
		else {
			error = 1001;
		}
		if (error != 0) {
			throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, error,
					"Executing DDE command failed:" +
							"\n\tserver= " + server +
							"\n\ttopic= " + topic +
							"\n\tcommand= " + command,
					null ));
		}
	}
	
	private static native int ddeExecute(String server, String topic, String command);
	
	
//	public static void main(String[] args) {
//		int error = ddeExecute("acroview", "control",
//				"[DocOpen(\"C:\\test.pdf\")][FileOpen(\"C:\\test.pdf\")]");
//		// Try [DocClose("test.pdf")], but must be opened by DDE (not user)
//		// Also, [MenuitemExecute("GoBack")] works in Acrobat (full)
//		System.out.println("Error: " + error);
//	}
	
}
