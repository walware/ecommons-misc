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
import org.eclipse.debug.core.model.IThread;
import org.eclipse.debug.core.model.IValue;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;

import de.walware.jcommons.collections.ImList;


@NonNullByDefault
public interface IEvaluationResult {
	
	
	int SKIPPED= IStatus.CANCEL | 0x10;
	
	
	String getExpressionText();
	
	IThread getThread();
	
	int getStatus();
	
	@Nullable IValue getValue();
	
	@Nullable ImList<@NonNull String> getMessages();
	
	
	void free();
	
}
