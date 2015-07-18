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

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;


@FixMethodOrder
public class OffsetStringParserInputTest {
	
	
	private OffsetStringParserInput input;
	
	
	@Test
	public void initRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(100, 200);
		
		assertEquals(100, this.input.getStartIndex());
		assertEquals(200, this.input.getStopIndex());
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStart() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(49, 400);
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStop() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, 801);
	}
	
	@Test (expected= IllegalArgumentException.class)
	public void initRegionIllegalLength() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(800, 400);
	}
	
	public void initNegativIndex() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s, -50);
		this.input.init(-50, 750);
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initNegativIndexInvalidStart() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s, -50);
		this.input.init(-51, 750);
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initNegativIndexInvalidStop() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s, -50);
		this.input.init(-50, 751);
	}
	
	@Test
	public void read() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, 800);
		
		assertChars(s, 50, 800);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
		assertEquals(TextParserInput.EOF, this.input.get(s.length() + 0x10000));
		
		assertEquals(s.length() - 50, this.input.getBuffer().length);
	}
	
	@Test
	public void readRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(100, 200);
		
		assertChars(s, 100, 200);
		
		assertEquals(TextParserInput.EOF, this.input.get(200));
		
		assertEquals(s.length() - 50, this.input.getBuffer().length);
	}
	
	@Test
	public void updateBuffer() {
		final String s= COUNTER_STRING;
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, s.length());
		
		readConsume(s, 50, s.length(), 100);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertEquals(TextParserInput.DEFAULT_BUFFER_SIZE, this.input.getBuffer().length);
	}
	
	@Test
	public void increaseBuffer() {
		final String s= COUNTER_STRING;
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, s.length());
		
		assertChars(s, 50, s.length());
		// check increased buffer completely:
		assertChars(s, 50, s.length());
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
	}
	
	@Test
	public void consume1() {
		final String s= COUNTER_STRING;
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, s.length());
		
		readConsume(s, 50, s.length(), 1);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
	}
	
	@Test
	public void combined() {
		final String s= COUNTER_STRING;
		this.input= new OffsetStringParserInput(s.substring(50), 50);
		this.input.init(50, s.length());
		
		readConsume(s, 50, s.length(), 2351);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertTrue(0x1000 >= this.input.getBuffer().length);
	}
	
	@Test
	public void empty() {
		final String s= "";
		this.input= new OffsetStringParserInput(s, 50);
		this.input.init(50, 50);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
	}
	
	
	protected void readConsume(final String expected, int begin, final int end, final int consume) {
		while (begin < end) {
			final int l= Math.min(end - begin, consume);
			assertEquals(begin, this.input.getIndex());
			assertEquals(0, this.input.getLengthInSource(0));
			assertChars(expected, begin, begin + l);
			assertEquals(l, this.input.getLengthInSource(l));
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
