/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
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
 * Text parser input for string representing only a part of the source.
 */
public final class OffsetStringParserInput extends TextParserInput implements CharSequence {
	
	
	private final String source;
	
	private final int sourceOffset;
	
	
	public OffsetStringParserInput(final String source, final int sourceOffset,
			final int defaultBufferSize) {
		super(defaultBufferSize);
		this.source= source;
		this.sourceOffset= sourceOffset;
	}
	
	public OffsetStringParserInput(final String source, final int sourceOffset) {
		this(source, sourceOffset,
				Math.min(source.length(), DEFAULT_BUFFER_SIZE) );
	}
	
	
	@Override
	protected int getSourceStartIndex() {
		return this.sourceOffset;
	}
	
	@Override
	protected int getSourceLength() {
		return this.source.length();
	}
	
	@Override
	protected String getSourceString() {
		return this.source;
	}
	
	@Override
	protected int getSourceStringIndex() {
		return this.sourceOffset;
	}
	
	
	@Override
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		final int length= Math.min(recommendLength, getStopIndex() - index);
		this.source.getChars(index - this.sourceOffset, index - this.sourceOffset + length, buffer, 0);
		setBuffer(buffer, 0, length);
	}
	
	
/*- CharSequence ---------------------------------------------------------------------------------*/
	
	@Override
	public int length() {
		return this.sourceOffset + this.source.length();
	}
	
	@Override
	public char charAt(final int index) {
		return this.source.charAt(index - this.sourceOffset);
	}
	
	@Override
	public String subSequence(final int start, final int end) {
		return this.source.substring(this.sourceOffset + start, this.sourceOffset + end);
	}
	
}
