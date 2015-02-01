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
package org.eclipse.nebula.widgets.nattable.group.command;

import java.util.List;

import org.eclipse.nebula.widgets.nattable.command.AbstractLayerCommandHandler;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupModel.ColumnGroup;
import org.eclipse.nebula.widgets.nattable.group.ColumnGroupReorderLayer;
import org.eclipse.nebula.widgets.nattable.layer.ILayer;
import org.eclipse.nebula.widgets.nattable.reorder.command.MultiColumnReorderCommand;


public class GroupMultiColumnReorderCommandHandler extends AbstractLayerCommandHandler<MultiColumnReorderCommand> {

	private final ColumnGroupReorderLayer columnGroupReorderLayer;

	public GroupMultiColumnReorderCommandHandler(ColumnGroupReorderLayer columnGroupReorderLayer) {
		this.columnGroupReorderLayer = columnGroupReorderLayer;
	}
	
	public Class<MultiColumnReorderCommand> getCommandClass() {
		return MultiColumnReorderCommand.class;
	}

	@Override
	protected boolean doCommand(MultiColumnReorderCommand command) {
		long toColumnPosition = command.getToColumnPosition();
		
		ILayer underlyingLayer = columnGroupReorderLayer.getUnderlyingLayer();
		long toColumnIndex = underlyingLayer.getColumnIndexByPosition(toColumnPosition);

		List<Long> fromColumnPositions = command.getFromColumnPositions();
		
		ColumnGroupModel model = columnGroupReorderLayer.getModel();
		
		if (updateModel(underlyingLayer, toColumnIndex, fromColumnPositions, model)) {
			return underlyingLayer.doCommand(command);
		} else {
			return false;
		}
	}

	private boolean updateModel(ILayer underlyingLayer, long toColumnIndex, List<Long> fromColumnPositions, ColumnGroupModel model) {
		// Moving INTO a group
		if (model.isPartOfAGroup(toColumnIndex)) {
			ColumnGroup toColumnGroup = model.getColumnGroupByIndex(toColumnIndex);
			String toGroupName = toColumnGroup.getName();
			if (model.isPartOfAnUnbreakableGroup(toColumnIndex)) {
				return false;
			}
			
			for (Long fromColumnPosition : fromColumnPositions) {
				long fromColumnIndex = underlyingLayer.getColumnIndexByPosition(fromColumnPosition.longValue());
				ColumnGroup fromColumnGroup = model.getColumnGroupByIndex(fromColumnIndex);

				// If 'from' index not already present in the 'to' group
				if (fromColumnGroup != toColumnGroup) {
					if (fromColumnGroup != null) {
						fromColumnGroup.removeColumn(fromColumnIndex);
					}
					model.addColumnsIndexesToGroup(toGroupName, fromColumnIndex);
				}
			}
			return true;
		}
		
		// Moving OUT OF a group
		if (!model.isPartOfAGroup(toColumnIndex)) {
			for (Long fromColumnPosition : fromColumnPositions) {
				// Remove from model - if present
				long fromColumnIndex = underlyingLayer.getColumnIndexByPosition(fromColumnPosition.longValue());
				ColumnGroup fromColumnGroup = model.getColumnGroupByIndex(fromColumnIndex);
				
				if (fromColumnGroup != null && !fromColumnGroup.removeColumn(fromColumnIndex)) {
					return false;
				}
			}
			return true;
		}
		
		return true;
	}
}
