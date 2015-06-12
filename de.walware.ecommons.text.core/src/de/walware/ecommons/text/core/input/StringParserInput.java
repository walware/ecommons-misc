/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.input;


/**
 * Text parser input for source string.
 */
public class StringParserInput extends TextParserInput implements CharSequence {
	
	
	private String source;
	
	
	public StringParserInput(final int defaultBufferSize) {
		super(defaultBufferSize);
	}
	
	public StringParserInput() {
		this(DEFAULT_BUFFER_SIZE);
	}
	
	public StringParserInput(final String source) {
		this(Math.min(source.length(), DEFAULT_BUFFER_SIZE));
		
		this.source= source;
	}
	
	
	public StringParserInput reset(final String source) {
		this.source= source;
		
		super.reset();
		
		return this;
	}
	
	@Override
	public StringParserInput init() {
		super.init();
		
		return this;
	}
	
	@Override
	public StringParserInput init(final int startIndex, final int stopIndex) {
		super.init(startIndex, stopIndex);
		
		return this;
	}
	
	
	@Override
	protected String getSourceString() {
		return this.source;
	}
	
	@Override
	protected int getSourceLength() {
		return this.source.length();
	}
	
	
	@Override
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		final int length= Math.min(recommendLength, getStopIndex() - index);
		this.source.getChars(index, index + length, buffer, 0);
		setBuffer(buffer, 0, length);
	}
	
	
/*- CharSequence ---------------------------------------------------------------------------------*/
	
	@Override
	public int length() {
		return this.source.length();
	}
	
	@Override
	public char charAt(final int index) {
		return this.source.charAt(index);
	}
	
	@Override
	public String subSequence(final int start, final int end) {
		return this.source.substring(start, end);
	}
	
}
