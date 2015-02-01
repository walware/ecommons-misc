/*******************************************************************************
 * Copyright (c) 2012-2015 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.style.editor.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;

public class DisplayColumnStyleEditorCommand extends AbstractContextFreeCommand {

	public final long columnPosition;
	public final long rowPosition;
	private final ILayer layer;
	private final IConfigRegistry configRegistry;

	public DisplayColumnStyleEditorCommand(ILayer natLayer, IConfigRegistry configRegistry, long columnPosition, long rowPosition) {
		this.layer = natLayer;
		this.configRegistry = configRegistry;
		this.columnPosition = columnPosition;
		this.rowPosition = rowPosition;
	}
	
	public ILayer getNattableLayer() {
		return layer;
	}

	public IConfigRegistry getConfigRegistry() {
		return configRegistry;
	}
}
