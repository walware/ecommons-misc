/*******************************************************************************
 * Copyright (c) 2007-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.databinding;


/**
 * Validator for integers.
 * 
 * @deprecated replaced by {@link IntegerValidator}
 */
@Deprecated
public class NumberValidator extends IntegerValidator {
	
	
	public NumberValidator(final int min, final int max, final String message) {
		super(min, max, false, message);
	}
	
	public NumberValidator(final int min, final int max, final boolean allowEmpty, final String message) {
		super(min, max, allowEmpty, message);
	}
	
}
