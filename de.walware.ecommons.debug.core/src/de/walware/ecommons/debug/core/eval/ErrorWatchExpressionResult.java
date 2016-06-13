/*=============================================================================#
 # Copyright (c) 2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.eval;

import java.util.Collection;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;


@NonNullByDefault
public class ErrorWatchExpressionResult implements IWatchExpressionResult {
	
	
	private final String expression;
	
	private final String[] messages;
	
	
	public ErrorWatchExpressionResult(final String expression, final String message) {
		this.expression= expression;
		this.messages= new String[] { message };
	}
	
	public ErrorWatchExpressionResult(final String expression, final Collection<String> messages) {
		this.expression= expression;
		this.messages= messages.toArray(new String[messages.size()]);
	}
	
	
	@Override
	public String getExpressionText() {
		return this.expression;
	}
	
	@Override
	public boolean hasErrors() {
		return true;
	}
	
	@Override
	public @Nullable IValue getValue() {
		return null;
	}
	
	@Override
	public String[] getErrorMessages() {
		return this.messages;
	}
	
	@Override
	public @Nullable DebugException getException() {
		return null;
	}
	
}
