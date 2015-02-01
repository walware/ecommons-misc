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
package org.eclipse.nebula.widgets.nattable.painter.cell.decorator;


import static org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils.safe;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.GraphicsUtils;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.util.GUIHelper;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;

import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;

/**
 * Decorator for rendering the cell with beveled borders (button look).
 * It is possible to render the beveled borders to look like the cell is uplifted or sunk.
 * The default is to render it uplifted.
 */
public class BeveledBorderDecorator extends CellPainterWrapper {

	/**
	 * Flag to determine whether the cell borders should be painted uplift or sunk.
	 */
	private boolean uplift = true;
	
	/**
	 * 
	 * @param interiorPainter The painter which should be wrapped by this decorator.
	 */
	public BeveledBorderDecorator(ICellPainter interiorPainter) {
		super(interiorPainter);
	}

	/**
	 * 
	 * @param interiorPainter The painter which should be wrapped by this decorator.
	 * @param uplift Flag to determine whether the cell borders should be painted uplift or sunk.
	 * 			By default this flag is set to <code>true</code>. Set it to <code>false</code> if
	 * 			the cell should be rendered sunk.
	 */
	public BeveledBorderDecorator(ICellPainter interiorPainter, boolean uplift) {
		super(interiorPainter);
		this.uplift = uplift;
	}
	
	public long getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return super.getPreferredWidth(cell, gc, configRegistry) + 4;
	}
	
	public long getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		return super.getPreferredHeight(cell, gc, configRegistry) + 4;
	}

	@Override
	public Rectangle getWrappedPainterBounds(ILayerCell cell, GC gc, Rectangle bounds, IConfigRegistry configRegistry) {
		return new Rectangle(bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
	}
	
	public void paintCell(ILayerCell cell, GC gc, Rectangle adjustedCellBounds, IConfigRegistry configRegistry) {
		Rectangle interiorBounds = getWrappedPainterBounds(cell, gc, adjustedCellBounds, configRegistry);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		// Save GC settings
		Color originalForeground = gc.getForeground();
		
		//TODO: Need to look at the border style
		
		org.eclipse.swt.graphics.Rectangle rect = safe(adjustedCellBounds);
		// Up
		gc.setForeground(uplift ? GUIHelper.COLOR_WIDGET_LIGHT_SHADOW : GUIHelper.COLOR_WIDGET_DARK_SHADOW);
		gc.drawLine(rect.x, rect.y, rect.x + rect.width - 1, rect.y);
		gc.drawLine(rect.x, rect.y, rect.x, rect.y + rect.height - 1);

		gc.setForeground(uplift ? GUIHelper.COLOR_WIDGET_HIGHLIGHT_SHADOW : GUIHelper.COLOR_WIDGET_NORMAL_SHADOW);
		gc.drawLine(rect.x + 1, rect.y + 1, rect.x + rect.width - 1, rect.y + 1);
		gc.drawLine(rect.x + 1, rect.y + 1, rect.x + 1, rect.y + rect.height - 1);

		// Down
		gc.setForeground(uplift ? GUIHelper.COLOR_WIDGET_DARK_SHADOW : GUIHelper.COLOR_WIDGET_LIGHT_SHADOW);
		gc.drawLine(rect.x, rect.y + rect.height - 1, rect.x + rect.width - 1, rect.y + rect.height - 1);
		gc.drawLine(rect.x + rect.width - 1, rect.y, rect.x + rect.width - 1, rect.y + rect.height - 1);

		gc.setForeground(uplift ? GUIHelper.COLOR_WIDGET_NORMAL_SHADOW : GUIHelper.COLOR_WIDGET_HIGHLIGHT_SHADOW);
		gc.drawLine(rect.x, rect.y + rect.height - 2, rect.x + rect.width - 1, rect.y + rect.height - 2);
		gc.drawLine(rect.x + rect.width - 2, rect.y, rect.x + rect.width - 2, rect.y + rect.height - 2);
		
		// Restore GC settings
		gc.setForeground(originalForeground);
	}
	
}
