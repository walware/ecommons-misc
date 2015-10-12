/*******************************************************************************
 * Copyright (c) 2000-2013 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package de.walware.ecommons.text.core.treepartitioner;

import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.DEFAULT_ROOT;
import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.T2;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.junit.Before;
import org.junit.Test;


/**
 * Equivalent to JFace's FastPartitionerTest (prefereOpen= false)
 */
public class PartitionerJFaceTest {
	
	
	protected static TestPartitionNodeType nextType(final TestPartitionNodeType type) {
		if (type == DEFAULT_ROOT) {
			return T2;
		}
		else {
			return DEFAULT_ROOT;
		}
	}
	
	
	protected IDocument doc;
	protected TreePartitioner partitioner;
	
	
	@Before
	public void setUp() {
		this.doc= new Document();
		this.partitioner= new TreePartitioner("default",
				new TestPartitionNodeScanner(),
				TestPartitionNodeType.CONTENT_TYPES_IDS );
		this.doc.setDocumentPartitioner(this.partitioner);
		this.partitioner.connect(this.doc);
	}
	
	
	@Test
	public void getPartition() {
		this.doc.set("docu     ment[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26 };
		assertGetPartition(offsets);
	}
	
	@Test
	public void getPartition_EmptyMiddle() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26, 26, 39 };
		assertGetPartition(offsets);
	}
	
	@Test
	public void getPartition_EmptyStart() {
		this.doc.set("[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 0, 13 };
		assertGetPartition(offsets);
	}
	
	@Test
	public void getPartition_EmptyEnd() {
		this.doc.set("docu     ment[* comment *]");
		
		final int[] offsets= new int[] { 13, 26 };
		assertGetPartition(offsets);
	}
	
	@Test
	public void getContentType() {
		this.doc.set("docu     ment[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26 };
		assertGetContentType(offsets);
	}
	
	@Test
	public void getContentType_EmptyMiddle() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26, 26, 39 };
		assertGetContentType(offsets);
	}
	
	@Test
	public void getContentType_EmptyStart() {
		this.doc.set("[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 0, 13 };
		assertGetContentType(offsets);
	}
	
	@Test
	public void getContentType_EmptyEnd() {
		this.doc.set("docu     ment[* comment *]");
		
		final int[] offsets= new int[] { 13, 26 };
		assertGetContentType(offsets);
	}
	
	@Test
	public void computePartitioning() {
		this.doc.set("docu     ment[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26 };
		assertComputePartitioning(offsets);
	}
	
	@Test
	public void computePartitioning_EmptyMiddle() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26, 26, 39 };
		assertComputePartitioning(offsets);
	}
	
	@Test
	public void computePartitioning_EmptyStart() {
		this.doc.set("[* comment *]docu     ment");
		
		final int[] offsets= new int[] { 0, 13 };
		assertComputePartitioning(offsets);
	}
	
	@Test
	public void computePartitioning_EmptyEnd() {
		this.doc.set("docu     ment[* comment *]");
		
		final int[] offsets= new int[] { 13, 26 };
		assertComputePartitioning(offsets);
	}
	
	@Test
	public void computePartitioning_SubrangeBeforeBoundaries() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26, 26 };
		assertComputePartitioning(12, 38, offsets, TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	@Test
	public void computePartitioning_SubrangeOnBoundaries() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 13, 26, 26, 39 };
		assertComputePartitioning(13, 39, offsets, TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	@Test
	public void computePartitioning_SubrangeOnBoundaries2() {
		this.doc.set("[* comment *][* comment *][* comment *]");
		
		final int[] offsets= new int[] { 13, 26 };
		assertComputePartitioning(13, 26, offsets, TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	@Test
	public void computePartitioning_SubrangeAfterBoundaries() {
		this.doc.set("docu     ment[* comment *][* comment *]docu     ment");
		
		final int[] offsets= new int[] { 26, 26, 39 };
		assertComputePartitioning(14, 40, offsets, T2);
	}
	
	@Test
	public void computePartitioning_SubrangeInBoundaries1() {
		this.doc.set("[* comment *]");
		
		final int[] offsets= new int[] { };
		assertComputePartitioning(1, 12, offsets, T2);
	}
	
	public void computePartitioning_SubrangeInBoundaries2() {
		this.doc.set("docu     ment");
		
		final int[] offsets= new int[] { };
		assertComputePartitioning(1, 12, offsets, TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	
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
	
	protected void assertComputePartitioning(final int[] offsets) {
		assertComputePartitioning(0, this.doc.getLength(), offsets, TestPartitionNodeType.DEFAULT_ROOT);
	}
	
	protected void assertComputePartitioning(final int startOffset, final int endOffset, final int[] offsets,
			final TestPartitionNodeType startType) {
		final ITypedRegion[] regions= this.partitioner.computePartitioning(startOffset, endOffset - startOffset);
		
		TestPartitionNodeType type= startType;
		int previousOffset= startOffset;
		
		int j= 0;
		for (int i= 0; i <= offsets.length; i++) {
			final int currentOffset= (i == offsets.length) ? endOffset : offsets[i];
			if (currentOffset - previousOffset != 0) { // don't do empty partitions
				final ITypedRegion region= regions[j++];
				
				TreePartitionerTest.assertTypedRegion(previousOffset, currentOffset, type.getPartitionType(),
						region );
			}
			
			// advance
			type= nextType(type);
			previousOffset= currentOffset;
		}
	}
	
	
	protected void assertEqualPartition(final int offset, final int end, final ITreePartitionNodeType type) {
		final int from= offset;
		final int to= end - 1;
		for (int i= from; i <= to; i++) {
			TreePartitionerTest.assertTypedRegion(offset, end, type.getPartitionType(),
					this.partitioner.getPartition(i) );
			TreePartitionerTest.assertTypedRegion(offset, end, type.getPartitionType(),
					this.partitioner.getPartition(i, false) );
		}
	}
	
	protected void assertEqualPartitionType(final int offset, final int end, final ITreePartitionNodeType type) {
		final int from= offset;
		final int to= end - 1;
		for (int i= from; i <= to; i++) {
			assertEquals(type.getPartitionType(),
					this.partitioner.getContentType(i) );
			assertEquals(type.getPartitionType(),
					this.partitioner.getContentType(i, false) );
		}
	}
	
}
