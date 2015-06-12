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

import org.junit.Test;

public class TextParserInputMatchTest {
	
	
	private StringParserInput input;
	
	
	@Test
	public void get() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput(s);
		this.input.init();
		
		this.input.consume('a');
		assertEquals(true, this.input.get(0) == 'a');
	}
	
	@Test
	public void matches1() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput(s);
		this.input.init();
		
		this.input.consume('a');
		assertEquals(true, this.input.matches(0, 'a'));
		assertEquals(true, this.input.matches(1, 'b'));
		assertEquals(true, this.input.matches(2, 'c'));
		assertEquals(false, this.input.matches(0, 'x'));
		assertEquals(false, this.input.matches(3, 'c'));
	}
	
	@Test
	public void matches2() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput(s);
		this.input.init();
		
		this.input.consume('a');
		assertEquals(true, this.input.matches(0, 'a', 'b'));
		assertEquals(true, this.input.matches(1, 'b', 'c'));
		assertEquals(true, this.input.matches(2, 'c', 'd'));
		assertEquals(false, this.input.matches(0, 'x', 'b'));
		assertEquals(false, this.input.matches(3, 'c', 'e'));
		assertEquals(false, this.input.matches(3, 'd', 'd'));
	}
	
	@Test
	public void matches3() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput(s);
		this.input.init();
		
		this.input.consume('a');
		assertEquals(true, this.input.matches(0, 'a', 'b', 'c'));
		assertEquals(true, this.input.matches(1, 'b', 'c', 'd'));
		assertEquals(true, this.input.matches(2, 'c', 'd', 'e'));
		assertEquals(false, this.input.matches(0, 'x', 'b', 'c'));
		assertEquals(false, this.input.matches(3, 'c', 'e', 'f'));
		assertEquals(false, this.input.matches(3, 'd', 'e', 'e'));
	}
	
	@Test
	public void matchesArray() {
		final String s= COUNTER_STRING;
		this.input= new StringParserInput(s);
		this.input.init();
		
		this.input.consume('a');
		assertEquals(true, this.input.matches(0, new char[] { }));
		assertEquals(true, this.input.matches(0, new char[] { 'a' }));
		assertEquals(true, this.input.matches(0, new char[] { 'a', 'b', 'c', 'd' }));
		assertEquals(true, this.input.matches(1, new char[] { 'b' }));
		assertEquals(true, this.input.matches(1, new char[] { 'b', 'c', 'd', 'e' }));
		assertEquals(true, this.input.matches(2, new char[] { 'c', 'd', 'e', 'f' }));
		assertEquals(false, this.input.matches(0, new char[] { 'x', 'b', 'c', 'd' }));
		assertEquals(false, this.input.matches(3, new char[] { 'c', 'e', 'f', 'g' }));
		assertEquals(false, this.input.matches(3, new char[] { 'd', 'e', 'f', 'f' }));
	}
	
	
}
