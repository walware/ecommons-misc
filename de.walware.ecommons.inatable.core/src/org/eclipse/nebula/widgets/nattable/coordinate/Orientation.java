/*******************************************************************************
 * Copyright (c) 2013-2015 Stephan Wahlbrink and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 ******************************************************************************/

package org.eclipse.nebula.widgets.nattable.coordinate;


public enum Orientation {
	
	
	/**
	 * The horizontal orientation (columns)
	 */
	HORIZONTAL,
	
	/**
	 * The vertical orientation (rows)
	 */
	VERTICAL();
	
	
	public Orientation getOrthogonal() {
		return (this == HORIZONTAL) ? VERTICAL : HORIZONTAL;
	}
	
}
