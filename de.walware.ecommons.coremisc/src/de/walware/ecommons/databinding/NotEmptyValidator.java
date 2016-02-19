/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osgi.util.NLS;


/**
 * Simple validator to make sure the string value is not empty
 */
public class NotEmptyValidator implements IValidator {
	
	
	private final String fMessage;
	
	private final IValidator fNested;
	
	
	public NotEmptyValidator(final String message) {
		fMessage = message;
		fNested = null;
	}
	
	public NotEmptyValidator(final String label, final IValidator validator) {
		fMessage = NLS.bind("{0} is missing.", label);
		fNested = validator;
	}
	
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			final String s = ((String) value).trim();
			if (s.length() > 0) {
				return (fNested != null) ? fNested.validate(value) : ValidationStatus.ok();
			}
		}
		return ValidationStatus.error(fMessage);
	}
	
}
