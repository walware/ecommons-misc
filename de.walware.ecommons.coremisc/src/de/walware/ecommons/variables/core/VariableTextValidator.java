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

package de.walware.ecommons.variables.core;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.variables.core.VariableText2.Severities;


public class VariableTextValidator implements IValidator {
	
	
	private final VariableText2 variableResolver;
	
	private final String message;
	
	
	public VariableTextValidator(final VariableText2 variableResolver, final String message) {
		this.variableResolver= variableResolver;
		this.message= message;
	}
	
	
	@Override
	public IStatus validate(final Object value) {
		try {
			this.variableResolver.validate((String) value, Severities.CHECK_SYNTAX, null);
			return ValidationStatus.ok();
		}
		catch (final CoreException e) {
			final IStatus status= e.getStatus();
			return new Status(status.getSeverity(), status.getPlugin(),
					NLS.bind(this.message, status.getMessage()) );
		}
	}
	
}
