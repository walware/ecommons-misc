/*******************************************************************************
 * Copyright (c) 2012 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
package org.eclipse.nebula.widgets.nattable.grid.data;

import org.eclipse.nebula.widgets.nattable.data.IDataProvider;

public class DefaultRowHeaderDataProvider implements IDataProvider {
	
	protected final IDataProvider bodyDataProvider;

	public DefaultRowHeaderDataProvider(IDataProvider bodyDataProvider) {
		this.bodyDataProvider = bodyDataProvider;
	}
	
	public long getColumnCount() {
		return 1;
	}

	public long getRowCount() {
		return bodyDataProvider.getRowCount();
	}

	public Object getDataValue(long columnIndex, long rowIndex) {
		return Long.valueOf(rowIndex + 1);
	}

	public void setDataValue(long columnIndex, long rowIndex, Object newValue) {
		throw new UnsupportedOperationException();
	}
	
}
