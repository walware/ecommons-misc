/*=============================================================================#
 # Copyright (c) 2014-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.treepartitioner;

import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.text.core.ITextRegion;


public interface ITreePartitionNode extends ITextRegion {
	
	
	ITreePartitionNodeType getType();
	
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><b>Warning:</b> region of the tree partition node is mutable due to document changes.
	 * </p>
	 */
	@Override
	int getOffset();
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><b>Warning:</b> region of the tree partition node is mutable due to document changes.
	 * </p>
	 */
	@Override
	int getEndOffset();
	
	/**
	 * {@inheritDoc}
	 * 
	 * <p><b>Warning:</b> region of the tree partition node is mutable due to document changes.
	 * </p>
	 */
	@Override
	int getLength();
	
	
	ITreePartitionNode getParent();
	
	int getChildCount();
	
	ITreePartitionNode getChild(int idx);
	
	int indexOfChild(int offset);
	int indexOfChild(ITreePartitionNode child);
	
	void addAttachment(Object data);
	void removeAttachment(Object data);
	ImList<Object> getAttachments();
	
}
