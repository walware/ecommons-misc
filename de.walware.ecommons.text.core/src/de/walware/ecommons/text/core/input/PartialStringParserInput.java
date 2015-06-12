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
 * Text parser input for string representing only a part of the source.
 */
public class PartialStringParserInput extends TextParserInput implements CharSequence {
	
	
	private final String partSource;
	
	private final int partIndex;
	
	
	public PartialStringParserInput(final String partSource, final int partIndex,
			final int defaultBufferSize) {
		super(defaultBufferSize);
		this.partSource= partSource;
		this.partIndex= partIndex;
	}
	
	public PartialStringParserInput(final String partSource, final int partIndex) {
		this(partSource, partIndex,
				Math.min(partSource.length(), DEFAULT_BUFFER_SIZE) );
	}
	
	
	@Override
	public TextParserInput init(final int startIndex, final int stopIndex) {
		if (startIndex < this.partIndex) {
			throw new IndexOutOfBoundsException("startIndex= " + startIndex); //$NON-NLS-1$
		}
		return super.init(startIndex, stopIndex);
	}
	
	@Override
	protected String getSourceString() {
		return this.partSource;
	}
	
	@Override
	protected int getSourceStringIndex() {
		return this.partIndex;
	}
	
	@Override
	protected int getSourceLength() {
		return this.partIndex + this.partSource.length();
	}
	
	
	@Override
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		final int length= Math.min(recommendLength, getStopIndex() - index);
		this.partSource.getChars(index - this.partIndex, index - this.partIndex + length, buffer, 0);
		setBuffer(buffer, 0, length);
	}
	
	
/*- CharSequence ---------------------------------------------------------------------------------*/
	
	@Override
	public int length() {
		return this.partIndex + this.partSource.length();
	}
	
	@Override
	public char charAt(final int index) {
		return this.partSource.charAt(index - this.partIndex);
	}
	
	@Override
	public String subSequence(final int start, final int end) {
		return this.partSource.substring(this.partIndex + start, this.partIndex + end);
	}
	
}
