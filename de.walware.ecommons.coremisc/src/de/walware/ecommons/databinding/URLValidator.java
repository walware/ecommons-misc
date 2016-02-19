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

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;


/**
 * Validator for URLs.
 */
public class URLValidator implements IValidator {
	
	
	private final String fLabel;
	
	
	public URLValidator(final String label) {
		fLabel = label;
	}
	
	
	@Override
	public IStatus validate(final Object value) {
		if (value instanceof String) {
			try {
				new URL((String) value);
				return ValidationStatus.ok();
			}
			catch (final MalformedURLException e) {
				return ValidationStatus.error(fLabel + " is invalid: " + e.getLocalizedMessage());
			}
		}
		throw new IllegalStateException("Unsupported value type: " + value.getClass().toString());
	}
	
}
