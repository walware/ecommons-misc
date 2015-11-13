/*******************************************************************************
 * Copyright (c) 2000, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.text.core.treepartitioner;

import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.text.core.TextRegion;


public class TreePartition extends TextRegion implements ITypedRegion {
	
	
	private final ITreePartitionNode node;
	
	
	/**
	 * Creates a typed region based on the given specification.
	 * 
	 * @param beginOffset the beginning offset, inclusive.
	 * @param endOffset the ending offset, exclusive.
	 * @param type the type of the region.
	 */
	public TreePartition(final int beginOffset, final int endOffset, final ITreePartitionNode node) {
		super(beginOffset, endOffset);
		
		if (node == null) {
			throw new NullPointerException("node"); //$NON-NLS-1$
		}
		this.node= node;
	}
	
	
	@Override
	public String getType() {
		return this.node.getType().getPartitionType();
	}
	
	public ITreePartitionNode getTreeNode() {
		return this.node;
	}
	
	
	@Override
	public int hashCode() {
		return super.hashCode() | getType().hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof TreePartition)) {
			return false;
		}
		return (super.equals(obj)
				&& getType().equals(((TreePartition) obj).getType()) );
	}
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder(getType());
		sb.append(": "); //$NON-NLS-1$
		appendIntervalString(sb);
		return sb.toString();
	}
	
}
