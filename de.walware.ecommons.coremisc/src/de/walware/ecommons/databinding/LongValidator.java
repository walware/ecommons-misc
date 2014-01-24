/*=============================================================================#
 # Copyright (c) 2007-2014 Stephan Wahlbrink (WalWare.de) and others.
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
 * Validator for long integers.
 */
public class LongValidator implements IValidator {
	
	
	private final NumberFormat fFormatter;
	private final long fMin;
	private final long fMax;
	private final boolean fAllowEmpty;
	private final String fMessage;
	
	
	public LongValidator(final long min, final long max, final String message) {
		this(min, max, false, message);
	}
	
	public LongValidator(final long min, final long max, final boolean allowEmpty, final String message) {
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
				final long n = number.longValue();
				if (n >= fMin && n <= fMax) {
					return Status.OK_STATUS;
				}
				// return range message
			}
		}
		return ValidationStatus.error(fMessage);
	}
	
}
