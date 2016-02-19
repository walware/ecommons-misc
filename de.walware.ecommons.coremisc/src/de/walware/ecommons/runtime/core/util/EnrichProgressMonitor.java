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

package de.walware.ecommons.runtime.core.util;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;


public class EnrichProgressMonitor extends ProgressMonitorWrapper {
	
	
	private String prefix;
	
	
	public EnrichProgressMonitor(final IProgressMonitor monitor) {
		super(monitor);
	}
	
	
	public void setPrefix(final String prefix) {
		this.prefix= prefix;
	}
	
	@Override
	public void setTaskName(String name) {
		final String prefix= this.prefix;
		if (prefix != null) {
			if (name != null) {
				super.setTaskName(prefix + name);
			}
			else {
				super.setTaskName(prefix);
			}
		}
		else {
			super.setTaskName(name);
		}
	}
	
}
