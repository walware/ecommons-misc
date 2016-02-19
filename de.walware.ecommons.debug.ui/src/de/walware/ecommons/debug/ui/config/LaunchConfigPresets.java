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

package de.walware.ecommons.debug.ui.config;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;


public class LaunchConfigPresets {
	
	
	public static final String UNDEFINED_VALUE= "!UNDEFINED!"; //$NON-NLS-1$
	
	
	private static final String NAME_ATTR_NAME= "Preset.name"; //$NON-NLS-1$
	
	
	static boolean isInternalArgument(final String name) {
		return (name.startsWith("Preset.")); //$NON-NLS-1$
	}
	
	static String getName(final ILaunchConfiguration preset) {
		try {
			return preset.getAttribute(NAME_ATTR_NAME, (String) null);
		}
		catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	private final ILaunchConfigurationType type;
	
	private final List<ILaunchConfiguration> presets= new ArrayList<>();
	
	
	public LaunchConfigPresets(final String typeId) {
		this.type= DebugPlugin.getDefault().getLaunchManager().getLaunchConfigurationType(typeId);
	}
	
	
	public ILaunchConfigurationWorkingCopy add(final String name) {
		if (name == null) {
			throw new NullPointerException("name"); //$NON-NLS-1$
		}
		try {
			final ILaunchConfigurationWorkingCopy config= this.type.newInstance(null, "template").getWorkingCopy(); //$NON-NLS-1$
			config.setAttribute(NAME_ATTR_NAME, name);
			
			this.presets.add(config);
			
			return config;
		}
		catch (final CoreException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ImList<ILaunchConfiguration> toList() {
		return ImCollections.toList(this.presets);
	}
	
}
