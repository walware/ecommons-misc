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
package org.eclipse.nebula.widgets.nattable.tree.command;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.tree.TreeLayer;

public class TreeExpandCollapseCommandHandler extends AbstractLayerCommandHandler<TreeExpandCollapseCommand> {

	private final TreeLayer treeLayer;

	public TreeExpandCollapseCommandHandler(TreeLayer treeLayer) {
		this.treeLayer = treeLayer;
	}
	
	public Class<TreeExpandCollapseCommand> getCommandClass() {
		return TreeExpandCollapseCommand.class;
	}

	@Override
	protected boolean doCommand(TreeExpandCollapseCommand command) {
		long parentIndex = command.getParentIndex();
		this.treeLayer.expandOrCollapseIndex(parentIndex);
		return true;
	}

}
