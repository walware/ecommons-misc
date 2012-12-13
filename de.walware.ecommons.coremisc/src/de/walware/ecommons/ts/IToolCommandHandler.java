/*******************************************************************************
 * Copyright (c) 2007-2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ts;

import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;


/**
 * A handler is plugged into a tool and is called by its tool service on special
 * events or tool commands.
 * For example a tool handler can enable the interaction with the GUI for the tool.
 * 
 * A single command handler can support multiple commands.
 * 
 * @since 1.1
 */
public interface IToolCommandHandler {
	
	
	/**
	 * Executes the specified command
	 * 
	 * @param id the ID of the command
	 * @param service the tool service calling the action
	 * @param data the action data for input (parameters) and output (return values)
	 * @param monitor the progress monitor
	 * 
	 * @return the status
	 */
	IStatus execute(String id, IToolService service, Map<String, Object> data,
			IProgressMonitor monitor) throws CoreException;
	
}
