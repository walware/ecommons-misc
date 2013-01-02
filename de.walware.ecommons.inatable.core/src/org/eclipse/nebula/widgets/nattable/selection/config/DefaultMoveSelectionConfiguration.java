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
package org.eclipse.nebula.widgets.nattable.selection.config;

import org.eclipse.nebula.widgets.nattable.config.AbstractLayerConfiguration;
import org.eclipse.nebula.widgets.nattable.selection.SelectionLayer;

/**
 * Configure the behavior when the selection is moved. Example: by using arrow keys.<br/>
 * This default configuration moves by cell.<br/>
 * 
 * {@link MoveSelectionCommand} are fired by the {@link DefaultSelectionBindings}.<br/>
 * An suitable handler can be plugged in to handle the move commands as required.<br/>
 * 
 * @see MoveRowSelectionCommandHandler
 */
public class DefaultMoveSelectionConfiguration extends AbstractLayerConfiguration<SelectionLayer>{

	@Override
	public void configureTypedLayer(SelectionLayer layer) {
//		layer.registerCommandHandler(new MoveCellSelectionCommandHandler(layer));
	}

}
