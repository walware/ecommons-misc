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
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ui.util.ImageRegistryUtil;

import de.walware.ecommons.debug.ui.ECommonsDebugUIResources;


public class ECommonsDebugUIPlugin extends AbstractUIPlugin {
	
	
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
	
	@Override
	protected void initializeImageRegistry(final ImageRegistry reg) {
		final ImageRegistryUtil util= new ImageRegistryUtil(this);
		
		util.register(ECommonsDebugUIResources.OBJ_VARIABLE_PARTITION, ImageRegistryUtil.T_OBJ, "variable_partition.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OBJ_VARIABLE_ITEM, ImageRegistryUtil.T_OBJ, "variable_item.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OBJ_VARIABLE_DIM, ImageRegistryUtil.T_OBJ, "variable_dim.png"); //$NON-NLS-1$
		
		util.register(ECommonsDebugUIResources.OVR_BREAKPOINT_INSTALLED, ImageRegistryUtil.T_OVR, "installed.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OVR_BREAKPOINT_INSTALLED_DISABLED, ImageRegistryUtil.T_OVR, "installed-disabled.png"); //$NON-NLS-1$
		
		util.register(ECommonsDebugUIResources.OVR_BREAKPOINT_CONDITIONAL, ImageRegistryUtil.T_OVR, "conditional.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OVR_BREAKPOINT_CONDITIONAL_DISABLED, ImageRegistryUtil.T_OVR, "conditional-disabled.png"); //$NON-NLS-1$
		
		util.register(ECommonsDebugUIResources.OVR_METHOD_BREAKPOINT_ENTRY, ImageRegistryUtil.T_OVR, "entry.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OVR_METHOD_BREAKPOINT_ENTRY_DISABLED, ImageRegistryUtil.T_OVR, "entry-disabled.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OVR_METHOD_BREAKPOINT_EXIT, ImageRegistryUtil.T_OVR, "exit.png"); //$NON-NLS-1$
		util.register(ECommonsDebugUIResources.OVR_METHOD_BREAKPOINT_EXIT_DISABLED, ImageRegistryUtil.T_OVR, "exit-disabled.png"); //$NON-NLS-1$
	}
	
}
