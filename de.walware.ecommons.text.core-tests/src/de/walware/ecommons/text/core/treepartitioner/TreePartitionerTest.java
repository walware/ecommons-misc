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

import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.DEFAULT_ROOT;
import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.T1;
import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.T2;
import static de.walware.ecommons.text.core.treepartitioner.TestPartitionNodeType.T3;
import static org.junit.Assert.assertEquals;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;


@FixMethodOrder
public class TreePartitionerTest {
	
	protected static void assertTypedRegion(final int offset, final int end, final String type, final ITypedRegion actual) {
		assertEquals(offset, actual.getOffset());
		assertEquals(end - offset, actual.getLength());
		assertEquals(type, actual.getType());
	}
	
	protected static void assertTypedRegion(final ITypedRegion expected, final ITypedRegion actual) {
		assertEquals(expected.getOffset(), actual.getOffset());
		assertEquals(expected.getLength(), actual.getLength());
		assertEquals(expected.getType(), actual.getType());
	}
	
	protected static void assertTypedRegions(final ITypedRegion[] expected, final ITypedRegion[] actual) {
		Assert.assertEquals("partitions.length", expected.length, actual.length);
		
		for (int i= 0; i < expected.length; i++) {
			try {
				assertTypedRegion(expected[i], actual[i]);
			}
			catch (final AssertionError e) {
				throw new ArrayComparisonFailure("partitions ", e, i);
			}
		}
	}
	
	
	private static class TestSet {
		
		final String content;
		
		final ExpectedPartition[] partitions;
		
		final ExpectedPartition[] zeroLengthPartitions;
		
		
		public TestSet(final String content,
				final ExpectedPartition[] partitions, final ExpectedPartition[] zeroLengthPartitions) {
			this.content= content;
			this.partitions= partitions;
			this.zeroLengthPartitions= zeroLengthPartitions;
		}
		
		
	}
	
	
	private final TestSet TEST_1= new TestSet("docu     ment{type 1} [type 2](type 3)docu     ment",
			new ExpectedPartition[] {
					new ExpectedPartition(0, 13, DEFAULT_ROOT),
					new ExpectedPartition(13, 8, T1),
					new ExpectedPartition(21, 1, DEFAULT_ROOT),
					new ExpectedPartition(22, 8, T2),
					new ExpectedPartition(30, 8, T3),
					new ExpectedPartition(38, 13, DEFAULT_ROOT)
			},
			new ExpectedPartition[] {
					new ExpectedPartition(0, 13, DEFAULT_ROOT, true, true),
					new ExpectedPartition(13, 8, T1, false, false),
					new ExpectedPartition(21, 1, DEFAULT_ROOT, true, true),
					new ExpectedPartition(22, 8, T2, false, false),
					new ExpectedPartition(30, 0, DEFAULT_ROOT, true, true),
					new ExpectedPartition(30, 8, T3, false, false),
					new ExpectedPartition(38, 13, DEFAULT_ROOT, true, true)
			}
	);
	
	private final TestSet TEST_NESTED= new TestSet("docu     ment{type 1[type 2(type 3)(type 3)]type 1}",
			new ExpectedPartition[] {
					new ExpectedPartition(0, 13, DEFAULT_ROOT),
					new ExpectedPartition(13, 7, T1),
					new ExpectedPartition(20, 7, T2),
					new ExpectedPartition(27, 8, T3),
					new ExpectedPartition(35, 8, T3),
					new ExpectedPartition(43, 1, T2),
					new ExpectedPartition(44, 7, T1),
			},
			new ExpectedPartition[] {
					new ExpectedPartition(0, 13, DEFAULT_ROOT, true, true),
					new ExpectedPartition(13, 7, T1, false, true),
					new ExpectedPartition(20, 7, T2, false, true),
					new ExpectedPartition(27, 8, T3, false, false),
					new ExpectedPartition(35, 0, T2, true, true),
					new ExpectedPartition(35, 8, T3, false, false),
					new ExpectedPartition(43, 1, T2, true, false),
					new ExpectedPartition(44, 7, T1, true, false),
					new ExpectedPartition(51, 0, DEFAULT_ROOT, true, true)
			}
	);
	
	
	protected IDocument doc;
	protected TreePartitioner partitioner;
	
	
	@Before
	public void setUp() {
		this.doc= new Document();
		this.partitioner= new TreePartitioner(new TestPartitionNodeScanner(),
				TestPartitionNodeType.CONTENT_TYPES_IDS );
		this.doc.setDocumentPartitioner(this.partitioner);
		this.partitioner.connect(this.doc);
	}
	
	
	@Test
	public void computePartitioning_Test1() throws BadLocationException {
		this.doc.set(this.TEST_1.content);
		assertTypedRegions(this.TEST_1.partitions,
				this.partitioner.computePartitioning(0, this.doc.getLength(), false) );
	}
	
	@Test
	public void computePartitioningZeroLength_Test1() throws BadLocationException {
		this.doc.set(this.TEST_1.content);
		assertTypedRegions(this.TEST_1.zeroLengthPartitions,
				this.partitioner.computePartitioning(0, this.doc.getLength(), true) );
	}
	
	@Test
	public void getPartition_Test1() throws BadLocationException {
		this.doc.set(this.TEST_1.content);
		assertGetPartition(this.TEST_1.partitions, false);
	}
	
	@Test
	public void getPartitionZeroLength_Test1() throws BadLocationException {
		this.doc.set(this.TEST_1.content);
		assertGetPartition(this.TEST_1.zeroLengthPartitions, true);
	}
	
	
	@Test
	public void computePartitiong_Nested() {
		this.doc.set(this.TEST_NESTED.content);
		assertTypedRegions(this.TEST_NESTED.partitions,
				this.partitioner.computePartitioning(0, this.doc.getLength(), false) );
	}
	
	@Test
	public void computePartitiongZeroLength_Nested() {
		this.doc.set(this.TEST_NESTED.content);
		assertTypedRegions(this.TEST_NESTED.zeroLengthPartitions,
				this.partitioner.computePartitioning(0, this.doc.getLength(), true) );
	}
	
	@Test
	public void getPartition_Nested() throws BadLocationException {
		this.doc.set(this.TEST_NESTED.content);
		assertGetPartition(this.TEST_NESTED.partitions, false);
	}
	
	@Test
	public void getPartitionZeroLength_Nested() throws BadLocationException {
		this.doc.set(this.TEST_NESTED.content);
		assertGetPartition(this.TEST_NESTED.zeroLengthPartitions, true);
	}
	
	
	protected void assertGetPartition(final ExpectedPartition[] expected, final boolean zeroLength) {
		for (int i= 0; i < expected.length; i++) {
			final ExpectedPartition iPartition= expected[i];
			final int start= (iPartition.prefereAtStart()) ?
					iPartition.getOffset() :
					(iPartition.getOffset() + 1);
			final int end= (iPartition.prefereAtEnd()) ?
					(iPartition.getOffset() + iPartition.getLength()) :
					(iPartition.getOffset() + iPartition.getLength() - 1);
			assertGetPartition(expected[i], start, end, zeroLength);
		}
	}
	
	protected void assertGetPartition(final ITypedRegion expected, final int offset, final int last, final boolean zeroLength) {
		for (int i= offset; i <= last; i++) {
			assertTypedRegion(expected,
					this.partitioner.getPartition(i, zeroLength) );
		}
	}
	
}
