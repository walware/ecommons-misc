/*=============================================================================#
 # Copyright (c) 2010-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

import de.walware.ecommons.debug.internal.ui.ECommonsDebugUIPlugin;


public class ECommonsDebugUIResources {
	
	
	private static final String NS= "de.walware.ecommons.debug"; //$NON-NLS-1$
	
	
	public static final String OBJ_VARIABLE_PARTITION= NS + "image/obj/VariablePartition"; //$NON-NLS-1$
	public static final String OBJ_VARIABLE_ITEM= NS + "image/obj/VariableItem"; //$NON-NLS-1$
	public static final String OBJ_VARIABLE_DIM= NS + "image/obj/VariableDim"; //$NON-NLS-1$
	
	public static final String OVR_BREAKPOINT_INSTALLED= NS + "image/ovr/Breakpoint.Installed"; //$NON-NLS-1$
	public static final String OVR_BREAKPOINT_INSTALLED_DISABLED= NS + "image/ovr/Breakpoint.Installed.disabled"; //$NON-NLS-1$
	
	public static final String OVR_BREAKPOINT_CONDITIONAL= NS + "image/ovr/Breakpoint.Conditional"; //$NON-NLS-1$
	public static final String OVR_BREAKPOINT_CONDITIONAL_DISABLED= NS + "image/ovr/Breakpoint.Conditional.disabled"; //$NON-NLS-1$
	
	public static final String OVR_METHOD_BREAKPOINT_ENTRY= NS + "image/ovr/Breakpoint.MethodEntry"; //$NON-NLS-1$
	public static final String OVR_METHOD_BREAKPOINT_ENTRY_DISABLED= NS + "image/ovr/Breakpoint.MethodEntry.disabled"; //$NON-NLS-1$
	public static final String OVR_METHOD_BREAKPOINT_EXIT= NS + "image/ovr/Breakpoint.MethodExit"; //$NON-NLS-1$
	public static final String OVR_METHOD_BREAKPOINT_EXIT_DISABLED= NS + "image/ovr/Breakpoint.MethodExit.disabled"; //$NON-NLS-1$
	
	
	public static final ECommonsDebugUIResources INSTANCE= new ECommonsDebugUIResources();
	
	
	private final ImageRegistry registry;
	
	
	private ECommonsDebugUIResources() {
		this.registry= ECommonsDebugUIPlugin.getInstance().getImageRegistry();
	}
	
	public ImageDescriptor getImageDescriptor(final String id) {
		return this.registry.getDescriptor(id);
	}
	
	public Image getImage(final String id) {
		return this.registry.get(id);
	}
	
}
