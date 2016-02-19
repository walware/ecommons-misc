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

package de.walware.ecommons.debug.internal.ui;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;


public class ECommonsDebugUIPlugin extends Plugin {
	
	
	/** The shared instance */
	private static ECommonsDebugUIPlugin instance;
	
	/**
	 * Returns the shared plug-in instance
	 *
	 * @return the shared instance
	 */
	public static ECommonsDebugUIPlugin getInstance() {
		return instance;
	}
	
	public static final void log(final IStatus status) {
		final Plugin plugin= getInstance();
		if (plugin != null) {
			plugin.getLog().log(status);
		}
	}
	
	
	private boolean started;
	
	
	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		instance= this;
		
		synchronized (this) {
			this.started= true;
		}
	}
	
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				this.started= false;
			}
		}
		finally {
			instance= null;
			super.stop(context);
		}
	}
	
	
	private void checkStarted() {
		if (!this.started) {
			throw new IllegalStateException("Plug-in is not started.");
		}
	}
	
}
