/*=============================================================================#
 # Copyright (c) 2005-2015 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 #
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;
import org.junit.Assert;

import junit.framework.TestCase;


public class ExclusivePositionUpdaterTest extends TestCase {
	
	private static final String CATEGORY= "testcategory";
	
	
	private IPositionUpdater updater;
	private Position pos;
	private IDocument doc;
	
	
	@Override
	protected void setUp() throws Exception {
		this.updater= new ExclusivePositionUpdater(CATEGORY);
		this.doc= new Document("ccccccccccccccccccccccccccccccccccccccccccccc");
		this.pos= new Position(5, 5);
		this.doc.addPositionUpdater(this.updater);
		this.doc.addPositionCategory(CATEGORY);
		this.doc.addPosition(CATEGORY, this.pos);
	}
	
	@Override
	protected void tearDown() throws Exception {
		this.doc.removePositionUpdater(this.updater);
		this.doc.removePositionCategory(CATEGORY);
	}
	
	
	// Delete, ascending by offset, length:
	
	public void testDeleteBefore() throws BadLocationException {
		this.doc.replace(2, 2, "");
		Assert.assertEquals(3, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteRightBefore() throws BadLocationException {
		this.doc.replace(3, 2, "");
		Assert.assertEquals(3, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteOverLeftBorder() throws BadLocationException {
		this.doc.replace(3, 6, "");
		Assert.assertEquals(3, this.pos.offset);
		Assert.assertEquals(1, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteOverLeftBorderTillRight() throws BadLocationException {
		this.doc.replace(4, 6, "");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testDeleted() throws BadLocationException {
		this.doc.replace(4, 7, "");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testDeleteAtOffset() throws BadLocationException {
		this.doc.replace(5, 1, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(4, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteAtOffset2() throws BadLocationException {
		this.doc.replace(5, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(3, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteAtOffsetTillRight() throws BadLocationException {
		this.doc.replace(5, 5, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(0, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteAtOffsetOverRightBorder() throws BadLocationException {
		this.doc.replace(5, 6, "");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testDeleteWithin() throws BadLocationException {
		this.doc.replace(6, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(3, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteAtRight() throws BadLocationException {
		this.doc.replace(8, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(3, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteOverRightBorder() throws BadLocationException {
		this.doc.replace(9, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(4, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteRightAfter() throws BadLocationException {
		this.doc.replace(10, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testDeleteAfter() throws BadLocationException {
		this.doc.replace(20, 2, "");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	
	// Add, ascending by offset:
	
	public void testAddBefore() throws BadLocationException {
		this.doc.replace(2, 0, "yy");
		Assert.assertEquals(7, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testAddRightBefore() throws BadLocationException {
		this.doc.replace(5, 0, "yy");
		Assert.assertEquals(7, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testAddWithin() throws BadLocationException {
		this.doc.replace(6, 0, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(7, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testAddWithin2() throws BadLocationException {
		this.doc.replace(9, 0, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(7, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testAddRightAfter() throws BadLocationException {
		this.doc.replace(10, 0, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testAddAfter() throws BadLocationException {
		this.doc.replace(20, 0, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	
	// Replace, ascending by offset, length:
	
	public void testReplaceBefore() throws BadLocationException {
		this.doc.replace(2, 2, "y");
		Assert.assertEquals(4, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceRightBefore() throws BadLocationException {
		this.doc.replace(2, 3, "y");
		Assert.assertEquals(3, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceLeftBorder() throws BadLocationException {
		this.doc.replace(4, 2, "yy");
		Assert.assertEquals(6, this.pos.offset);
		Assert.assertEquals(4, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceLeftBorderTillRight() throws BadLocationException {
		this.doc.replace(4, 6, "yy");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testReplaced1() throws BadLocationException {
		this.doc.replace(4, 7, "yy");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testReplaced2() throws BadLocationException {
		this.doc.replace(4, 7, "yyyyyyy");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testReplaced3() throws BadLocationException {
		this.doc.replace(4, 7, "yyyyyyyy");
		Assert.assertTrue(this.pos.isDeleted);
	}
	
	public void testReplaceAtOffset1() throws BadLocationException {
		this.doc.replace(5, 1, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(6, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceAtOffset2() throws BadLocationException {
		this.doc.replace(5, 4, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(3, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceAtOffsetTillRight() throws BadLocationException {
		this.doc.replace(5, 5, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(2, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceAtRight() throws BadLocationException {
		this.doc.replace(6, 4, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(3, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceRightBorder() throws BadLocationException {
		this.doc.replace(9, 2, "yy");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(4, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceRightAfter() throws BadLocationException {
		this.doc.replace(10, 2, "y");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
	public void testReplaceAfter() throws BadLocationException {
		this.doc.replace(20, 2, "y");
		Assert.assertEquals(5, this.pos.offset);
		Assert.assertEquals(5, this.pos.length);
		Assert.assertFalse(this.pos.isDeleted);
	}
	
}
