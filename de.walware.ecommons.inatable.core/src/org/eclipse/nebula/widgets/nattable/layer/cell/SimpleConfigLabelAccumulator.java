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
package org.eclipse.nebula.widgets.nattable.layer.cell;

import org.eclipse.nebula.widgets.nattable.layer.LabelStack;

public class SimpleConfigLabelAccumulator implements IConfigLabelAccumulator {

	private final String configLabel;

	public SimpleConfigLabelAccumulator(String configLabel) {
		this.configLabel = configLabel;
	}

	public void accumulateConfigLabels(LabelStack configLabels, long columnPosition, long rowPosition) {
		configLabels.addLabel(configLabel);
	}
	
}
