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
package org.eclipse.nebula.widgets.nattable.resize.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.layer.DataLayer;

public class RowResizeCommandHandler extends AbstractLayerCommandHandler<RowResizeCommand> {

	private final DataLayer dataLayer;

	public RowResizeCommandHandler(DataLayer dataLayer) {
		this.dataLayer = dataLayer;
	}
	
	public Class<RowResizeCommand> getCommandClass() {
		return RowResizeCommand.class;
	}

	@Override
	protected boolean doCommand(RowResizeCommand command) {
		final int newRowHeight = command.getNewHeight();
		dataLayer.setRowHeightByPosition(command.getRowPosition(), newRowHeight);
		return true;
	}

}
