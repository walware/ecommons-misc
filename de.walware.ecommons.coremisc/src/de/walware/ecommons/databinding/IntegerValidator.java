/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding;

import java.text.ParsePosition;

import com.ibm.icu.text.NumberFormat;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * Validator for integers.
 */
public class IntegerValidator implements IValidator {
	
	
	private final NumberFormat fFormatter;
	private final int fMin;
	private final int fMax;
	private final boolean fAllowEmpty;
	private final String fMessage;
	
	
	public IntegerValidator(final int min, final int max, final String message) {
		this(min, max, false, message);
	}
	
	public IntegerValidator(final int min, final int max, final boolean allowEmpty, final String message) {
		fMin = min;
		fMax = max;
		fAllowEmpty = allowEmpty;
		fMessage = message;
		fFormatter = NumberFormat.getIntegerInstance();
		fFormatter.setParseIntegerOnly(true);
	}
	
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = ((String) value).trim();
			if (fAllowEmpty && s.length() == 0) {
				return Status.OK_STATUS;
			}
			final ParsePosition result = new ParsePosition(0);
			final Number number = fFormatter.parse(s, result);
			if (result.getIndex() == s.length() && result.getErrorIndex() < 0) {
				final int n = number.intValue();
				if (n >= fMin && n <= fMax) {
					return Status.OK_STATUS;
				}
				// return range message
			}
		}
		return ValidationStatus.error(fMessage);
	}
	
}
