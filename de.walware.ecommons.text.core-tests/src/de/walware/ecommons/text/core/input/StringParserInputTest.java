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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.internal.ArrayComparisonFailure;


@FixMethodOrder
public class StringParserInputTest {
	
	
	protected static final String COUNTER_STRING;
	static {
		final char[] test= new char[0x4000];
		for (int i= 0; i < test.length; i++) {
			test[i]= (char) i;
		}
		COUNTER_STRING= new String(test);
	}
	
	
	private StringParserInput input;
	
	
	@Test
	public void init() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init();
		
		assertEquals(0, this.input.getStartIndex());
		assertEquals(s.length(), this.input.getStopIndex());
	}
	
	@Test
	public void initRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init(100, 200);
		
		assertEquals(100, this.input.getStartIndex());
		assertEquals(200, this.input.getStopIndex());
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStart() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init(-1, 400);
	}
	
	@Test (expected= IndexOutOfBoundsException.class)
	public void initRegionIllegalStop() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init(0, 801);
	}
	
	@Test (expected= IllegalArgumentException.class)
	public void initRegionIllegalLength() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init(800, 400);
	}
	
	@Test
	public void read() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init();
		
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
		assertEquals(TextParserInput.EOF, this.input.get(s.length() + 0x10000));
		
		assertEquals(s.length(), this.input.getBuffer().length);
	}
	
	@Test
	public void readRegion() {
		final String s= COUNTER_STRING.substring(0, 800);
		this.input= new StringParserInput(s);
		this.input.init(100, 200);
		
		assertChars(s, 100, 200);
		
		assertEquals(TextParserInput.EOF, this.input.get(200));
		
		assertEquals(s.length(), this.input.getBuffer().length);
	}
	
	@Test
	public void updateBuffer() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput();
		this.input.reset(s);
		this.input.init();
		
		readConsume(s, 0, s.length(), 100);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertEquals(TextParserInput.DEFAULT_BUFFER_SIZE, this.input.getBuffer().length);
	}
	
	@Test
	public void increaseBuffer() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput();
		this.input.reset(s);
		this.input.init();
		
		assertChars(s);
		// check increased buffer completely:
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
	}
	
	@Test
	public void consume1() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput();
		this.input.reset(s);
		this.input.init();
		
		readConsume(s, 0, s.length(), 1);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
	}
	
	@Test
	public void combined() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput();
		this.input.reset(s);
		this.input.init();
		
		readConsume(s, 0, s.length(), 2351);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		
		assertTrue(0x1000 >= this.input.getBuffer().length);
	}
	
	@Test
	public void empty() {
		final String s= "";
		this.input= new StringParserInput();
		this.input.reset(s);
		this.input.init();
		
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
