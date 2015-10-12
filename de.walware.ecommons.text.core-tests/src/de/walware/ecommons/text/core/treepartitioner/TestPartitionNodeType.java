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

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;

public class TestPartitionNodeType extends AbstractPartitionNodeType {
	
	
	public static final String DEFAULT_ID= "DEFAULT";
	public static final String T1_ID= "{}";
	public static final String T2_ID= "[]";
	public static final String T3_ID= "()";
	
	
	public static ImList<String> CONTENT_TYPES_IDS= ImCollections.newList(
			DEFAULT_ID,
			T1_ID, T2_ID, T3_ID
	);
	
	
	public static final TestPartitionNodeType DEFAULT_ROOT= new TestPartitionNodeType(DEFAULT_ID, 0);
	public static final TestPartitionNodeType T1= new TestPartitionNodeType(T1_ID, 1);
	public static final TestPartitionNodeType T2= new TestPartitionNodeType(T2_ID, 2);
	public static final TestPartitionNodeType T3= new TestPartitionNodeType(T3_ID, 3);
	
	
	private final String type;
	
	
	public TestPartitionNodeType(final String type, final int priority) {
		this.type= type;
	}
	
	@Override
	public String getPartitionType() {
		return this.type;
	}
	
}
