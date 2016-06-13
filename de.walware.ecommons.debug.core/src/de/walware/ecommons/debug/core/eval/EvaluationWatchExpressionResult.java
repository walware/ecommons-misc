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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.debug.core.model.IWatchExpressionResult;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.walware.jcommons.collections.ImList;


@NonNullByDefault
public class EvaluationWatchExpressionResult implements IWatchExpressionResult {
	
	
	private static final String[] NO_MESSAGES= new String[0];
	
	
	private final IEvaluationResult result;
	
	
	public EvaluationWatchExpressionResult(final IEvaluationResult result) {
		this.result= result;
		
		result.free();
	}
	
	
	@Override
	public String getExpressionText() {
		return this.result.getExpressionText();
	}
	
	@Override
	public boolean hasErrors() {
		return (this.result.getStatus() == IStatus.ERROR);
	}
	
	@Override
	public @Nullable IValue getValue() {
		return this.result.getValue();
	}
	
	@Override
	public String @Nullable[] getErrorMessages() {
		if (this.result.getStatus() >= IStatus.ERROR) {
			final ImList<@NonNull String> messages= this.result.getMessages();
			if (messages != null) {
				return messages.toArray(new String[messages.size()]);
			}
		}
		return NO_MESSAGES;
	}
	
	@Override
	public @Nullable DebugException getException() {
		return null;
	}
	
	
	@Override
	public int hashCode() {
		return this.result.hashCode();
	}
	
	@Override
	public boolean equals(@Nullable final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof EvaluationWatchExpressionResult) {
			return (this.result.equals(((EvaluationWatchExpressionResult) obj).result));
		}
		return false;
	}
	
	@Override
	public String toString() {
		return "EvaluationWatchExpressionResult = " + this.result.toString(); //$NON-NLS-1$
	}
	
}
