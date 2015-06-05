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

package de.walware.ecommons.text.core.treepartitioner;

import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;


public class ExpectedPartition extends Region implements ITypedRegion {
	
	
	private final TestPartitionNodeType type;
	
	private final boolean prefereAtStart;
	private final boolean prefereAtEnd;
	
	
	public ExpectedPartition(final int offset, final int length, final TestPartitionNodeType type) {
		this(offset, length, type, true, false);
	}
	
	public ExpectedPartition(final int offset, final int length, final TestPartitionNodeType type,
			final boolean prefereAtStart, final boolean prefereAtEnd) {
		super(offset, length);
		
		this.type= type;
		this.prefereAtStart= prefereAtStart;
		this.prefereAtEnd= prefereAtEnd;
	}
	
	
	public TestPartitionNodeType getNodeType() {
		return this.type;
	}
	
	@Override
	public String getType() {
		return this.type.getPartitionType();
	}
	
	public boolean prefereAtStart() {
		return this.prefereAtStart;
	}
	
	public boolean prefereAtEnd() {
		return this.prefereAtEnd;
	}
	
}
