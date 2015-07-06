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

package de.walware.ecommons.debug.core.util;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

import de.walware.ecommons.IDisposable;
import de.walware.ecommons.collections.CopyOnWriteListSet;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.debug.core.ECommonsDebugCore;
import de.walware.ecommons.debug.internal.core.ECommonsDebugCorePlugin;


public class LaunchConfigurationCollector implements ILaunchConfigurationListener, IDisposable {
	
	
	private final ILaunchConfigurationType type;
	
	private final CopyOnWriteListSet<ILaunchConfiguration> configurations= new CopyOnWriteListSet<>();
	
	
	public LaunchConfigurationCollector(final String typeId) {
		final ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		
		this.type= launchManager.getLaunchConfigurationType(typeId);
		
		launchManager.addLaunchConfigurationListener(this);
		
		try {
			for (ILaunchConfiguration configuration : launchManager.getLaunchConfigurations()) {
				launchConfigurationAdded(configuration);
			}
		}
		catch (final CoreException e) {
			log(e);
		}
	}
	
	
	@Override
	public void dispose() {
		final ILaunchManager launchManager= DebugPlugin.getDefault().getLaunchManager();
		
		if (launchManager != null) {
			launchManager.removeLaunchConfigurationListener(this);
		}
	}
	
	
	protected final ILaunchConfigurationType getLaunchConfigurationType() {
		return this.type;
	}
	
	protected boolean include(final ILaunchConfiguration configuration) throws CoreException {
		return this.type.equals(configuration.getType());
	}
	
	
	public final ImList<ILaunchConfiguration> getConfigurations() {
		return this.configurations.toList();
	}
	
	
	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		try {
			if (include(configuration)) {
				this.configurations.add(configuration);
			}
		}
		catch (CoreException e) {
			log(e);
		}
	}
	
	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		try {
			if (include(configuration)) {
				this.configurations.add(configuration);
			}
			else {
				this.configurations.remove(configuration);
			}
		}
		catch (CoreException e) {
			log(e);
		}
	}
	
	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		this.configurations.remove(configuration);
	}
	
	
	protected void log(final CoreException e) {
		ECommonsDebugCorePlugin.log(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
				"An error occurred when checking launch configurations.", e ));
	}
	
}
