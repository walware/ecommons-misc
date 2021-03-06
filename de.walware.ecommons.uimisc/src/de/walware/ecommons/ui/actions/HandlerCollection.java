/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.actions;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IHandler2;


/**
 * Utility to manage the command handler of a service layer (view, dialog, etc.).
 */
public class HandlerCollection {
	
	
	private final Map<String, IHandler2> handlers= new HashMap<>();
	
	
	public HandlerCollection() {
	}
	
	
	public void add(final String commandId, final IHandler2 handler) {
		if (commandId == null || handler == null) {
			throw new NullPointerException();
		}
		this.handlers.put(commandId, handler);
	}
	
	public IHandler2 get(final String commandId) {
		return this.handlers.get(commandId);
	}
	
	public void update(final Object evaluationContext) {
		for (final IHandler2 handler : this.handlers.values()) {
			handler.setEnabled(evaluationContext);
		}
	}
	
	
	public void dispose() {
		for (final IHandler2 handler : this.handlers.values()) {
			handler.dispose();
		}
		this.handlers.clear();
	}
	
}
