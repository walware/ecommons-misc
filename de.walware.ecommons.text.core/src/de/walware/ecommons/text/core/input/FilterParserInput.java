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


public abstract class FilterParserInput extends TextParserInput {
	
	
	private final TextParserInput source;
	
	private int[] bufferBeginIndexes;
	private int[] bufferEndIndexes;
	
	
	protected FilterParserInput(final TextParserInput source, final int defaultBufferSize) {
		super(defaultBufferSize);
		
		if (source == null) {
			throw new NullPointerException("source"); //$NON-NLS-1$
		}
		this.source= source;
		
		this.bufferBeginIndexes= new int[1];
		this.bufferEndIndexes= new int[0];
	}
	
	
	@Override
	protected void reset() {
		super.reset();
		
		this.bufferBeginIndexes[0]= 0;
	}
	
	
	@Override
	public FilterParserInput init() {
		super.init();
		
		return this;
	}
	
	@Override
	public FilterParserInput init(final int startIndex, final int stopIndex) {
		this.source.init(startIndex, stopIndex);
		
		this.bufferBeginIndexes[0]= this.source.getStartIndex();
		
		super.init(this.source.getStartIndex(), this.source.getStopIndex());
		
		return this;
	}
	
	public final TextParserInput getSource() {
		return this.source;
	}
	
	@Override
	protected int getSourceLength() {
		return this.source.getSourceLength();
	}
	
	@Override
	protected String getSourceString() {
		return this.source.getSourceString();
	}
	
	@Override
	protected int getSourceStringIndex() {
		return this.source.getSourceStringIndex();
	}
	
	
	@Override
	public final int getIndex(final int offset) {
		final int currentIdx= getIndexIdx();
		return this.bufferBeginIndexes[currentIdx + offset];
	}
	
	@Override
	public final int getLengthInSource(final int offset) {
		if (offset == 0) {
			return 0;
		}
		final int currentIdx= getIndexIdx();
		return this.bufferEndIndexes[currentIdx + offset - 1] - this.bufferBeginIndexes[currentIdx];
	}
	
	@Override
	public void consume(int offset) {
		if (offset == 0) {
			return;
		}
		int currentIdx= getIndexIdx();
		{	// we need bufferIndex for offset
			final int lastOffset= getEndIdx() - currentIdx - 1;
			if (offset > lastOffset) {
				if (lastOffset > 0x10) {
					setConsume(lastOffset, this.bufferBeginIndexes[currentIdx + lastOffset]);
					currentIdx= getIndexIdx();
					offset-= lastOffset;
				}
				if (!updateBuffer(offset + 1)) {
					int index= getStopIndex();
					if (index < 0) {
						index= this.bufferBeginIndexes[getEndIdx()];
					}
					setConsume(offset, index);
					return;
				}
			}
		}
		setConsume(offset, this.bufferBeginIndexes[currentIdx + offset]);
	}
	
	
	@Override
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		final int reused= copyBuffer0(buffer);
		final int[] begins= (buffer == getBuffer()) ? this.bufferBeginIndexes : new int[buffer.length + 1];
		final int[] ends= (buffer == getBuffer()) ? this.bufferEndIndexes : new int[buffer.length];
		final int currentIdx= getIndexIdx();
		if (reused > 0) {
			System.arraycopy(this.bufferBeginIndexes, currentIdx, begins, 0, reused + 1);
			System.arraycopy(this.bufferEndIndexes, currentIdx, ends, 0, reused);
		}
		else {
			begins[0]= this.bufferBeginIndexes[currentIdx];
		}
		
		final int end= read(this.source, buffer, begins, ends, reused, requiredLength, recommendLength);
		
		setBuffer(buffer, 0, end);
		this.bufferBeginIndexes= begins;
		this.bufferEndIndexes= ends;
		setConsume(0, this.bufferBeginIndexes[0]);
	}
	
	protected abstract int read(TextParserInput in, char[] buffer,
			int[] beginIndexes, int[] endIndexes,
			int beginIdx, int requiredEnd, int recommendEnd);
	
}
