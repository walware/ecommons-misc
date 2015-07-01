/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.databinding.core.util;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;

import de.walware.ecommons.databinding.core.DataStatus;


public class UpdateableErrorValidator implements IValidator {
	
	
	private final IValidator validator;
	
	
	public UpdateableErrorValidator(final IValidator validator) {
		if (validator == null) {
			throw new NullPointerException("validator"); //$NON-NLS-1$
		}
		this.validator= validator;
	}
	
	
	@Override
	public IStatus validate(final Object value) {
		final IStatus status= this.validator.validate(value);
		if (status != null && status.getSeverity() == IStatus.ERROR) {
			return new DataStatus(DataStatus.UPDATEABLE_ERROR, status.getMessage());
		}
		return status;
	}
	
}
