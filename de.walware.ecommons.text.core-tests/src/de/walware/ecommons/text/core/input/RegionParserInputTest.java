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

package de.walware.ecommons.text.core.input;

import static de.walware.ecommons.text.core.input.StringParserInputTest.COUNTER_STRING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;


@FixMethodOrder
public class RegionParserInputTest {
	
	
	private static String createRegionString(final String s, final ImList<? extends IRegion> regions) {
		final StringBuilder sb= new StringBuilder();
		for (final IRegion region : regions) {
			sb.append(s.substring(region.getOffset(), region.getOffset() + region.getLength()));
		}
		return sb.toString();
	}
	
	private static int[] createRegionIndexes(final ImList<? extends IRegion> regions) {
		int l= 0;
		for (final IRegion region : regions) {
			l+= region.getLength();
		}
		final int[] indexes= new int[l];
		int idx= 0;
		for (final IRegion region : regions) {
			int index= region.getOffset();
			for (int i= 0; i < region.getLength(); i++) {
				indexes[idx++]= index++;
			}
		}
		return indexes;
	}
	
	
	private RegionParserInput input;
	
	
	@Test
	public void init() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		this.input.init();
		
		assertEquals(0, this.input.getStartIndex());
		assertEquals(s.length(), this.input.getStopIndex());
	}
	
	@Test
	public void initRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		this.input.init(100, 200);
		
		assertEquals(100, this.input.getStartIndex());
		assertEquals(200, this.input.getStopIndex());
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStart() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		this.input.init(-1, 400);
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStop() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		this.input.init(0, 801);
	}
	
	@Test (expected= IllegalArgumentException.class)
	public void initRegionIllegalLength() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		this.input.init(800, 400);
	}
	
	@Test
	public void read() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(100, 100), new Region(300, 400) );
		this.input.reset(regions).init();
		final String rs= createRegionString(s, regions);
		
		assertChars(rs);
		
		assertEquals(TextParserInput.EOF, this.input.get(rs.length()));
		
		assertEquals(s.length(), this.input.getBuffer().length);
	}
	
	@Test
	public void readRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(100, 100), new Region(300, 400) );
		this.input.reset(regions).init(200, 500);
		
		assertChars(s, 300, 500);
		
		assertEquals(TextParserInput.EOF, this.input.get(200));
		
		assertEquals(s.length(), this.input.getBuffer().length);
	}
	
	@Test
	public void updateBuffer() {
		final String s= COUNTER_STRING;
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(0, 1000), new Region(2000, 1000), new Region(4000, 1000), new Region(6000, 1000) );
		this.input.reset(regions).init();
		
		final String rs= createRegionString(s, regions);
		final int[] ri= createRegionIndexes(regions);
		readConsume(rs, ri, 0, rs.length(), 100);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertEquals(TextParserInput.DEFAULT_BUFFER_SIZE, this.input.getBuffer().length);
	}
	
	@Test
	public void increaseBuffer() {
		final String s= COUNTER_STRING;
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(0, 1000), new Region(2000, 1000), new Region(4000, 1000), new Region(6000, 1000) );
		this.input.reset(regions).init();
		
		final String rs= createRegionString(s, regions);
		
		assertChars(rs);
		
		assertEquals(TextParserInput.EOF, this.input.get(rs.length()));
	}
	
	@Test
	public void consume1() {
		final String s= COUNTER_STRING;
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(0, 1000), new Region(2000, 1000), new Region(4000, 1000), new Region(6000, 1000) );
		this.input.reset(regions).init();
		
		final String rs= createRegionString(s, regions);
		final int[] ri= createRegionIndexes(regions);
		readConsume(rs, ri, 0, rs.length(), 1);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
	}
	
	@Test
	public void combined() {
		final String s= COUNTER_STRING;
		this.input= new RegionParserInput(s, ImCollections.<IRegion>emptyList());
		
		final ImList<? extends IRegion> regions= ImCollections.newList(
				new Region(5, 100), new Region(2000, 1000), new Region(4000, 777), new Region(6000, 1111) );
		this.input.reset(regions).init();
		
		final String rs= createRegionString(s, regions);
		final int[] ri= createRegionIndexes(regions);
		readConsume(rs, ri, 0, rs.length(), 2351);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertTrue(0x1000 >= this.input.getBuffer().length);
	}
	
	protected void readConsume(final String expected, final int[] indexes, int begin, final int end, final int consume) {
		while (begin < end) {
			final int l= Math.min(end - begin, consume);
			assertEquals(indexes[begin], this.input.getIndex());
			assertEquals(0, this.input.getLengthInSource(0));
			assertChars(expected, begin, begin + l);
			assertEquals(indexes[begin + l - 1] + 1 - indexes[begin], this.input.getLengthInSource(l));
			this.input.consume(l);
			begin+= l;
		}
	}
	
	protected void assertChars(final String expected) {
		assertChars(expected, 0, expected.length());
	}
	
	protected void assertChars(final String expected, final int begin, final int end) {
		for (int n= 0, index= begin; index < end; n++, index++) {
			final char eChar= expected.charAt(index);
			final int actual= this.input.get(n);
			try {
				assertEquals(eChar, actual);
			}
			catch (final AssertionError e) {
				throw new ArrayComparisonFailure("char ", e, index);
			}
		}
	}
	
}
