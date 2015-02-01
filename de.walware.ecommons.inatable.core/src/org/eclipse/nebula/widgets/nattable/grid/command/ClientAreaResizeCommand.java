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
package org.eclipse.nebula.widgets.nattable.grid.command;

import org.eclipse.swt.widgets.Scrollable;

import org.eclipse.nebula.widgets.nattable.command.AbstractContextFreeCommand;
import org.eclipse.nebula.widgets.nattable.coordinate.Rectangle;
import org.eclipse.nebula.widgets.nattable.swt.SWTUtil;

/**
 * Command that gives the layers access to ClientArea and the Scrollable 
 */
public class ClientAreaResizeCommand extends AbstractContextFreeCommand {
	
	/**
	 * The {@link Scrollable}, normally the NatTable itself.
	 */
	private Scrollable scrollable;
	
	/**
	 * This is the area within the client area that is used for percentage calculation.
	 * Without using a GridLayer, this will be the client area of the scrollable.
	 * On using a GridLayer this value will be overriden with the body region area.
	 */
	private Rectangle calcArea;

	public ClientAreaResizeCommand(Scrollable scrollable) {
		super();
		this.scrollable = scrollable;
	}

	public Scrollable getScrollable() {
		return scrollable;
	}
	
	public Rectangle getCalcArea() {
		if (calcArea == null) {
			return SWTUtil.toNatTable(scrollable.getClientArea());
		}
		return calcArea;
	}
	
	public void setCalcArea(Rectangle calcArea) {
		this.calcArea = calcArea;
	}
	
}
