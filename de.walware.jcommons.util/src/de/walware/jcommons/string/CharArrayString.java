/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.string;


/**
 * Open string class based on char array.
 * In contrast to the standard string class, it allows the exchange and modification 
 * the array and avoid copying of the source array.
 * 
 * The class is not prepared for concurrent access.
 */
public final class CharArrayString implements CharSequence {
	
	
	private static final char[] EMPTY_ARRAY= new char[0];
	
	
	private char[] array;
	private int offset;
	private int length;
	
	private int hashCode;
	
	
	public CharArrayString() {
		this.array= EMPTY_ARRAY;
	}
	
	public CharArrayString(final char[] array, final int offset, final int length) {
		set(array, offset, length);
	}
	
	public CharArrayString(final String s) {
		set(s);
	}
	
	public void clear() {
		this.array= EMPTY_ARRAY;
		this.offset= this.length= this.hashCode= 0;
	}
	
	public void set(final char[] array) {
		this.array= array;
		this.offset= 0;
		this.length= array.length;
		this.hashCode= 0;
	}
	
	public void set(final char[] array, final int offset, final int length) {
		this.array= array;
		this.offset= offset;
		this.length= length;
		this.hashCode= 0;
	}
	
	public void set(final String s) {
		this.array= s.toCharArray();
		this.offset= 0;
		this.length= this.array.length;
		this.hashCode= s.hashCode();
	}
	
	
	@Override
	public int length() {
		return this.length;
	}
	
	@Override
	public char charAt(final int index) {
		return this.array[this.offset+index];
	}
	
	@Override
	public CharSequence subSequence(final int start, final int end) {
		return new CharArrayString(this.array, this.offset + start, end - start);
	}
	
	
	@Override
	public int hashCode() {
		int hashCode= this.hashCode;
		if (hashCode == 0) {
			int length= this.length;
			final char[] array= this.array;
			int offset= this.offset;
			while (length-- != 0) {
				hashCode= 31 * hashCode + array[offset++];
			}
		}
		return hashCode;
	}
	
	public boolean contentEquals(final String s) {
		int length= this.length;
		if (length != s.length()
				|| (this.hashCode != 0 && this.hashCode != s.hashCode()) ) {
			return false;
		}
		final char[] array= this.array;
		int offset= this.offset;
		int i= 0;
		while (length-- != 0) {
			if (array[offset++] != s.charAt(i++)) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof CharArrayString)) {
			return false;
		}
		final CharArrayString other= (CharArrayString) obj;
		int length= this.length;
		if (length != other.length
				|| (this.hashCode != 0 && other.hashCode != 0 && this.hashCode != other.hashCode) ) {
			return false;
		}
		final char[] array1= this.array;
		int offset1= this.offset;
		final char[] array2= other.array;
		int offset2= other.offset;
		while (length-- > 0) {
			if (array1[offset1++] != array2[offset2++]) {
				return false;
			}
		}
		return true;
	}
	
	@Override
	public String toString() {
		return new String(this.array, this.offset, this.length);
	}
	
}
