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

import de.walware.ecommons.text.core.util.HtmlStripParserInput;
import de.walware.ecommons.text.core.util.HtmlUtils;
import de.walware.ecommons.text.core.util.HtmlUtils.Entity;


@FixMethodOrder
public class HtmlStripParserInputTest {
	
	
	private HtmlStripParserInput input;
	
	
	@Test
	public void skipTag() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		sb.insert(100, "<html>");
		sb.insert(200, "</html>");
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertChars(s, 0, 100);
		assertEquals(100, this.input.getLengthInSource(100));
		this.input.consume(100);
		// <html>
		assertEquals(106, this.input.getIndex());
		assertChars(s, 100, s.length());
		assertEquals(sb.length() - 106, this.input.getLengthInSource(s.length() - 100));
		this.input.consume(s.length() - 100);
		
		assertEquals(TextParserInput.EOF, this.input.get(0));
		assertEquals(0, this.input.getLengthInSource(0));
	}
	
	@Test
	public void skipTagAtStart() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		sb.insert(0, "<html></html>");
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertEquals(13, this.input.getIndex());
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
	}
	
	@Test
	public void skipTagWithAttr() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		sb.insert(100, "<html attr=\"abc\">");
		sb.insert(200, "<html attr=\'abc\'>");
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
	}
	
	@Test
	public void skipTagWithAttrSpecial() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		sb.insert(100, "<html attr=\"ab'c\">");
		sb.insert(200, "<html attr=\'ab\"c\'>");
		sb.insert(300, "<html attr=\"<ignore\">");
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
	}
	
	@Test
	public void htmlDoc() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		sb.insert(0, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\"\n" +
				"    \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">" +
				"<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"de\">");
		sb.insert(1000, "<!-- comment -->");
		sb.insert(1100, "<!--->");
		sb.insert(4000, "<a long=\"tag " + COUNTER_STRING.substring(1000, 3000) + "\">");
		sb.append("</html>");
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertChars(s);
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
		
		assertTrue(0x5000 > this.input.getBuffer().length);
	}
	
	private static int replaceByEntity(final StringBuilder sb, final Entity entity) {
		final int start= sb.indexOf(entity.getString());
		sb.replace(start, start + entity.getString().length(), '&' + entity.getName() + ';');
		return start;
	}
	
	@Test
	public void decodeEntity() {
		final String s= COUNTER_STRING;
		final StringBuilder sb= new StringBuilder(s);
		final int entityIdx= replaceByEntity(sb, HtmlUtils.getNamedEntity("angle"));
		
		this.input= new HtmlStripParserInput(sb.toString());
		this.input.init();
		
		assertChars(s);
		assertEquals(entityIdx, this.input.getLengthInSource(entityIdx));
		assertEquals(entityIdx + 7, this.input.getLengthInSource(entityIdx + 1));
		assertEquals(sb.length(), this.input.getLengthInSource(s.length()));
		
		assertEquals(TextParserInput.EOF, this.input.get(s.length()));
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
