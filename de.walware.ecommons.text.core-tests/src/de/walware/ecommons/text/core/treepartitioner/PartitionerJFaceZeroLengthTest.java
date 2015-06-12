/*******************************************************************************
 * Copyright (c) 2000-2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.text.core.treepartitioner;

import static de.walware.ecommons.text.core.treepartitioner.TreePartitionerTest.assertTypedRegion;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.ITypedRegion;


/**
 * Equivalent to JFace's DefaultPartitionerZeroLengthTest (prefereOpen= true)
 */
public class PartitionerJFaceZeroLengthTest extends PartitionerJFaceTest {
	
	
	@Override
	protected void assertGetContentType(final int[] offsets) {
		TestPartitionNodeType type= TestPartitionNodeType.DEFAULT_ROOT;
		int previousOffset= 0;
		for (int i= 0; i <= offsets.length; i++) {
			final int offset= (i == offsets.length) ? this.doc.getLength() : offsets[i];
			assertEqualPartitionType(previousOffset, offset, type);
			
			// advance
			type= nextType(type);
			previousOffset= offset;
		}
	}
	
	@Override
	protected void assertGetPartition(final int[] offsets) {
		TestPartitionNodeType type= TestPartitionNodeType.DEFAULT_ROOT;
		int previousOffset= 0;
		for (int i= 0; i <= offsets.length; i++) {
			final int offset= (i == offsets.length) ? this.doc.getLength() : offsets[i];
			assertEqualPartition(previousOffset, offset, type);
			
			// advance
			type= nextType(type);
			previousOffset= offset;
		}
	}
	
	@Override
	protected void assertComputePartitioning(final int startOffset, final int endOffset, final int[] offsets,
			final TestPartitionNodeType startType) {
		final ITypedRegion[] regions= this.partitioner.computePartitioning(startOffset, endOffset - startOffset, true);
		
		TestPartitionNodeType type= startType;
		int previousOffset= startOffset;
		
//		assertEquals(offsets.length + 1, regions.length);
		for (int i= 0; i <= offsets.length; i++) {
			final int currentOffset= (i == offsets.length) ? endOffset : offsets[i];
			final ITypedRegion region= regions[i];
			
			assertTypedRegion(previousOffset, currentOffset, type.getPartitionType(),
					region );
			
			// advance
			type= nextType(type);
			previousOffset= currentOffset;
		}
	}
	
	private boolean isOpenType(final ITreePartitionNodeType type) {
		return (type == TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	@Override
	protected void assertEqualPartition(final int offset, final int inclusiveEnd, final ITreePartitionNodeType type) {
		final int from= isOpenType(type) ? offset : offset + 1;
		final int to= isOpenType(type) ? inclusiveEnd : inclusiveEnd - 1;
		for (int i= from; i <= to; i++) {
			assertTypedRegion(offset, inclusiveEnd, type.getPartitionType(),
					this.partitioner.getPartition(i, true) );
		}
	}
	
	@Override
	protected void assertEqualPartitionType(final int offset, final int end, final ITreePartitionNodeType type) {
		final int from= isOpenType(type) ? offset : offset + 1;
		final int to= isOpenType(type) ? end : end - 1;
		for (int i= from; i <= to; i++) {
			assertEquals(type.getPartitionType(),
					this.partitioner.getContentType(i, true) );
		}
	}
	
}
