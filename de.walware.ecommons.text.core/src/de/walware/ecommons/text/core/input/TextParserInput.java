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

import de.walware.ecommons.string.CharArrayString;
import de.walware.ecommons.string.IStringFactory;


/**
 * Generic API for input of lexers etc.
 * 
 * <p>Subclasses have to fill and update the buffer.</p>
 * 
 * <p>Naming Convention:</p><ul>
 *   <li>'index' refers to absolute position in the original source.</li>
 *   <li>'offset' refers to relative char offset from the current position (0= current char, 1= next char, ...).</li>
 *   <li>'idx' refers to index in buffer (not public).</li>
 * </ul>
 **/
public abstract class TextParserInput {
	
	
	public static final int EOF= -1;
	
	
	protected static final int DEFAULT_BUFFER_SIZE= 0x800;
	
	protected static final char[] NO_INPUT= new char[0];
	
	
	private final CharArrayString tmpCharString= new CharArrayString();
	
	private int startIndex;
	private int stopIndex;
	
	private final int defaultBufferLength;
	private char[] buffer;
	
	private int currentIdx;
	private int endIdx;
	private int currentIndex;
	
	
	protected TextParserInput(final int defaultBufferLength) {
		this.defaultBufferLength= defaultBufferLength;
		this.buffer= NO_INPUT;
		this.stopIndex= Integer.MIN_VALUE;
		this.currentIndex= Integer.MIN_VALUE;
	}
	
	
	protected void reset() {
		this.startIndex= 0;
		this.stopIndex= Integer.MIN_VALUE;
		this.currentIndex= Integer.MIN_VALUE;
		this.currentIdx= 0;
		this.endIdx= 0;
	}
	
	public TextParserInput init() {
		init(0, Integer.MIN_VALUE);
		
		return this;
	}
	
	public TextParserInput init(final int startIndex, int stopIndex) {
		final int length= getSourceLength();
		if (length > 0) {
			if (stopIndex > length) {
				throw new IndexOutOfBoundsException("stopIndex= " + stopIndex); //$NON-NLS-1$
			}
			else if (stopIndex < 0) {
				stopIndex= length;
			}
			if (startIndex < 0 || startIndex > stopIndex) {
				throw new IndexOutOfBoundsException("startIndex= " + startIndex); //$NON-NLS-1$
			}
		}
		
		this.startIndex= startIndex;
		this.stopIndex= stopIndex;
		this.currentIndex= startIndex;
		this.currentIdx= 0;
		this.endIdx= 0;
		
		updateBuffer(0);
		
		return this;
	}
	
	
	/**
	 * Returns the length of the source text.
	 * 
	 * @return the length or <code>-1</code> for unknown
	 */
	protected int getSourceLength() {
		return -1;
	}
	
	/**
	 * Returns the source text as string, if possible.
	 * 
	 * @return the underlying text
	 */
	protected String getSourceString() {
		return null;
	}
	
	/**
	 * Returns the index of the {@link #getSourceString() source string} in the source text.
	 * 
	 * @return the index of the source string
	 */
	protected int getSourceStringIndex() {
		return 0;
	}
	
	
	/**
	 * Returns the start index of this input in the source.
	 * 
	 * @return the start index
	 */
	public final int getStartIndex() {
		return this.startIndex;
	}
	
	/**
	 * Returns the stop index of this input in the source (exclusive).
	 * 
	 * @return the stop index
	 */
	public final int getStopIndex() {
		return this.stopIndex;
	}
	
	/**
	 * Returns the current index in the source text.
	 * 
	 * @return the index in the source
	 */
	public final int getIndex() {
		return this.currentIndex;
	}
	
	
	/**
	 * Returns the character at the specified offset in the content.
	 * 
	 * @param offset the offset in the content
	 * @return the content character, or {@link #EOF} if outside of the content
	 */
	public final int get(final int offset) {
		int idx= this.currentIdx + offset;
		if (idx >= this.endIdx) {
			if (updateBuffer(offset + 1)) {
				idx= this.currentIdx + offset;
			}
			else {
				return EOF;
			}
		}
		return this.buffer[idx];
	}
	
	public final boolean matches(final int offset, final char c1) {
		int idx= this.currentIdx + offset;
		if (idx >= this.endIdx) {
			if (updateBuffer(offset + 1)) {
				idx= this.currentIdx + offset;
			}
			else {
				return false;
			}
		}
		return (this.buffer[idx] == c1);
	}
	
	public final boolean matches(final int offset, final char c1, final char c2) {
		int idx= this.currentIdx + offset;
		if (idx + 1 >= this.endIdx) {
			if (updateBuffer(offset + 2)) {
				idx= this.currentIdx + offset;
			}
			else {
				return false;
			}
		}
		return (this.buffer[idx] == c1 && this.buffer[++idx] == c2);
	}
	
	public final boolean matches(final int offset, final char c1, final char c2, final char c3) {
		int idx= this.currentIdx + offset;
		if (idx + 2 >= this.endIdx) {
			if (updateBuffer(offset + 3)) {
				idx= this.currentIdx + offset;
			}
			else {
				return false;
			}
		}
		return (this.buffer[idx] == c1 && this.buffer[++idx] == c2 && this.buffer[++idx] == c3);
	}
	
	public final boolean matches(int offset, final char[] sequence) {
		final int l= sequence.length;
		int idx= this.currentIdx + offset;
		if (idx + l > this.endIdx) {
			if (updateBuffer(offset + l)) {
				idx= this.currentIdx + offset;
			}
			else {
				return false;
			}
		}
		for (offset= 0; offset < l; offset++) {
			if (this.buffer[idx++] != sequence[offset]) {
				return false;
			}
		}
		return true;
	}
	
	
	/**
	 * Returns the length in source text of the content from the current index to the specified
	 * offset (exclusive).
	 * 
	 * @param offset the end offset in the content
	 * @return the length in the source
	 */
	public int getLengthInSource(final int offset) {
		return offset;
	}
	
	/**
	 * Forwards the current index to the specified offset.
	 * 
	 * @param offset the offset in content
	 */
	public void consume(final int offset) {
		setConsume(offset, this.currentIndex + offset);
	}
	
	protected final void setConsume(final int offset, final int index) {
		this.currentIdx+= offset;
		this.currentIndex= index;
	}
	
	protected boolean updateBuffer(final int requiredLength) {
		final int index= this.currentIndex;
		if (index + requiredLength > this.stopIndex) {
			return false;
		}
		
		char[] buffer= this.buffer;
		int recommendLength= this.defaultBufferLength;
		if (requiredLength > recommendLength) {
			recommendLength= ((requiredLength + 0x410) / 0x400) * 0x400;
		}
		if (buffer.length < recommendLength) {
			buffer= new char[recommendLength];
		}
		doUpdateBuffer(index, buffer, requiredLength, recommendLength);
		return (this.currentIdx + requiredLength <= this.endIdx);
	}
	
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		setBuffer(NO_INPUT, 0, 0);
	}
	
	/**
	 * Copies valid region of current char buffer to specified char buffer.
	 * The {@link #getIndex() current index} is copied to index 0 in the char buffer.
	 * 
	 * @param buffer char buffer to copy to.
	 * 
	 * @return length of valid region copied
	 */
	protected final int copyBuffer0(final char[] buffer) {
		final int l= this.endIdx - this.currentIdx;
		if (l > 0) {
			System.arraycopy(this.buffer, this.currentIdx, buffer, 0, l);
		}
		return l;
	}
	
	protected final char[] getBuffer() {
		return this.buffer;
	}
	
	/**
	 * Sets the char buffer.
	 * 
	 * @param buffer the char buffer
	 * @param currentIdx index in char buffer of {@link #getIndex() current index}
	 * @param length the length of valid region in char buffer
	 */
	protected final void setBuffer(final char[] buffer, final int currentIdx, final int length) {
		this.buffer= buffer;
		this.currentIdx= currentIdx;
		this.endIdx= checkEndIdx(currentIdx + length);
		this.tmpCharString.clear();
	}
	
	private int checkEndIdx(int idx) {
		if (this.stopIndex >= 0) {
			final int stopIdx= this.currentIdx - this.currentIndex + this.stopIndex;
			if (stopIdx < idx) {
				idx= stopIdx;
			}
		}
		return (idx > 0) ? idx : 0;
	}
	
	/**
	 * Returns index in char buffer of {@link #getIndex() current index}.
	 * 
	 * @return index in char buffer
	 */
	protected final int getIndexIdx() {
		return this.currentIdx;
	}
	
	protected final int getEndIdx() {
		return this.endIdx;
	}
	
	
	public final String getString(final int offset, final int length) {
		return new String(this.buffer, this.currentIdx + offset, length);
	}
	
	protected final CharArrayString getTmpString(final int offset, final int length) {
		this.tmpCharString.set(this.buffer, this.currentIdx + offset, length);
		return this.tmpCharString;
	}
	
	public final String getString(final int offset, final int length, final IStringFactory factory) {
		this.tmpCharString.set(this.buffer, this.currentIdx + offset, length);
		return factory.get(this.tmpCharString);
	}
	
	@Override
	public String toString() {
		if (getSourceString() != null) {
			final int offset= getSourceStringIndex();
			if (offset == 0) {
				return getSourceString();
			}
			else if (offset > 0) {
				final StringBuilder sb= new StringBuilder();
				sb.ensureCapacity(offset + getSourceString().length());
				sb.setLength(offset);
				sb.append(getSourceString());
				return sb.toString();
			}
			else {
				return getSourceString().substring(-offset);
			}
		}
		return super.toString();
	}
	
}
