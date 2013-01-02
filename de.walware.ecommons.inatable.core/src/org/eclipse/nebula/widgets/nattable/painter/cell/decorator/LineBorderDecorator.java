/*******************************************************************************
 * Copyright (c) 2012-2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.painter.cell.decorator;

import org.eclipse.nebula.widgets.nattable.config.IConfigRegistry;
import org.eclipse.nebula.widgets.nattable.layer.cell.ILayerCell;
import org.eclipse.nebula.widgets.nattable.painter.cell.CellPainterWrapper;
import org.eclipse.nebula.widgets.nattable.painter.cell.ICellPainter;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle;
import org.eclipse.nebula.widgets.nattable.style.BorderStyle.LineStyle;
import org.eclipse.nebula.widgets.nattable.style.CellStyleAttributes;
import org.eclipse.nebula.widgets.nattable.style.CellStyleUtil;
import org.eclipse.nebula.widgets.nattable.style.IStyle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;


public class LineBorderDecorator extends CellPainterWrapper {

	private final BorderStyle defaultBorderStyle;

	public LineBorderDecorator(ICellPainter interiorPainter) {
		this(interiorPainter, null);
	}
	
	public LineBorderDecorator(ICellPainter interiorPainter, BorderStyle defaultBorderStyle) {
		super(interiorPainter);
		this.defaultBorderStyle = defaultBorderStyle;
	}


	public int getPreferredWidth(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		int padding = borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		
		return super.getPreferredWidth(cell, gc, configRegistry) + (padding * 2);
	}
	
	public int getPreferredHeight(ILayerCell cell, GC gc, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		int padding = borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		
		return super.getPreferredHeight(cell, gc, configRegistry) + (padding * 2);
	}

	private BorderStyle getBorderStyle(ILayerCell cell, IConfigRegistry configRegistry) {
		IStyle cellStyle = CellStyleUtil.getCellStyle(cell, configRegistry);
		BorderStyle borderStyle = cellStyle.getAttributeValue(CellStyleAttributes.BORDER_STYLE);
		if (borderStyle == null) {
			borderStyle = defaultBorderStyle;
		}
		return borderStyle;
	}

	public void paintCell(ILayerCell cell, GC gc, Rectangle rectangle, IConfigRegistry configRegistry) {
		BorderStyle borderStyle = getBorderStyle(cell, configRegistry);
		
		int padding = borderStyle != null ? Math.max(borderStyle.getOffset() + borderStyle.getThickness(), 0) : 0;
		Rectangle interiorBounds =
			new Rectangle(
					rectangle.x + padding,
					rectangle.y + padding,
					rectangle.width - (padding * 2),
					rectangle.height - (padding * 2)
			);
		super.paintCell(cell, gc, interiorBounds, configRegistry);
		
		if (borderStyle == null || borderStyle.getThickness() <= 0) {
			return;
		}
		
		// Save GC settings
		Color originalForeground = gc.getForeground();
		int originalLineWidth = gc.getLineWidth();
		int originalLineStyle = gc.getLineStyle();
		
		int borderOffset = borderStyle.getOffset();
		int borderThickness = borderStyle.getThickness();
		Rectangle borderArea = new Rectangle(
						rectangle.x + borderOffset,
						rectangle.y + borderOffset,
						rectangle.width - (borderOffset * 2),
						rectangle.height - (borderOffset * 2)
				);
		{
			int shift = 0;
			int areaShift = 0;
			if ((borderThickness % 2) == 0) {
				shift = borderThickness / 2;
				areaShift = (shift * 2);
			} else {
				shift = borderThickness / 2;
				areaShift = (shift * 2) + 1;
			}
			borderArea.x += shift;
			borderArea.y += shift;
			borderArea.width -= areaShift;
			borderArea.height -= areaShift;
		}

		gc.setLineWidth(borderThickness);
		gc.setLineStyle(borderStyle.getLineStyle().toSWT());
		gc.setForeground(borderStyle.getColor());
		gc.drawRectangle(borderArea);

		// Restore GC settings
		gc.setForeground(originalForeground);
		gc.setLineWidth(originalLineWidth);
		gc.setLineStyle(originalLineStyle);
	}
	
}
