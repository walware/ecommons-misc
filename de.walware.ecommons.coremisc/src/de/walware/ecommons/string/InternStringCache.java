/*=============================================================================#
 # Copyright (c) 2008-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.string;


/**
 * A cache of fixed size, interning string
 */
public class InternStringCache implements IStringFactory {
	
	
	private static final int HASHSET_SIZE= 0x400;
	private static final int HASHSET_MASK= HASHSET_SIZE - 1; // bits true from right
	
	private static final int CHARTABLE_SIZE= 0x7F;
	private static final String[] CHARTABLE;
	static {
		CHARTABLE= new String[CHARTABLE_SIZE];
		for (char i= 0; i < CHARTABLE_SIZE; i++) {
			CHARTABLE[i]= String.valueOf(i).intern();
		}
	}
	
	
	private final String[] values;
	
	private final int maxCachedLength;
	
	
	public InternStringCache(final int maxCachedLength) {
		this.values= new String[HASHSET_MASK + 1];
		this.maxCachedLength= maxCachedLength;
	}
	
	
	@Override
	public String get(final CharArrayString s) {
		switch (s.length()) {
		case 0:
			return ""; //$NON-NLS-1$
		case 1:
			return getChar(s.charAt(0));
		default:
			if (s.length() > this.maxCachedLength) {
				return s.toString();
			}
			return getDefault(s);
		}
	}
	
	@Override
	public String get(final CharSequence s) {
		switch (s.length()) {
		case 0:
			return ""; //$NON-NLS-1$
		case 1:
			return getChar(s.charAt(0));
		default:
			if (s.length() > this.maxCachedLength) {
				return s.toString();
			}
			return getDefault(s);
		}
	}
	
	@Override
	public String get(final String s, final boolean isCompact) {
		switch (s.length()) {
		case 0:
			return ""; //$NON-NLS-1$
		case 1:
			return getChar(s.charAt(0), s);
		default:
			if (s.length() > this.maxCachedLength) {
				return (isCompact) ? s : new String(s);
			}
			return getDefault(s);
		}
	}
	
	
	private String getChar(final char c) {
		if (c >= 0 && c < CHARTABLE_SIZE) {
//			fStatCharmap++;
			return CHARTABLE[c];
		}
		else {
			final int i1= ((c - CHARTABLE_SIZE) & HASHSET_MASK); // hashCode= c
			final String s1= this.values[i1];
			if (s1 != null && s1.length() == 1 && s1.charAt(0) == c) {
//				fStatFound++;
				return s1;
			}
//			fStatSet++;
			return (this.values[i1]= String.valueOf(c).intern());
		}
	}
	
	private String getChar(final char c, final String s) {
		if (c >= 0 && c < CHARTABLE_SIZE) {
//			fStatCharmap++;
			return CHARTABLE[c];
		}
		else {
			final int i1= ((c - CHARTABLE_SIZE) & HASHSET_MASK); // hashCode= c
			final String s1= this.values[i1];
			if (s1 != null && s1.length() == 1 && s1.charAt(0) == c) {
//				fStatFound++;
				return s1;
			}
//			fStatSet++;
			return (this.values[i1]= s.intern());
		}
	}
	
	private int computeHash(final CharSequence s) {
		int hashCode= 0;
		int length= s.length();
		int index= 0;
		while (length-- != 0) {
			hashCode= 31 * hashCode + s.charAt(index++);
		}
		return hashCode;
	}
	
	private String getDefault(final CharArrayString s) {
		final int hashCode= s.hashCode();
		final int i1= (hashCode & HASHSET_MASK);
		final String s1= this.values[i1];
		if (s1 != null && s.contentEquals(s1)) {
//			fStatFound++;
			return s1;
		}
//		fStatSet++;
		return (this.values[i1]= s.toString().intern());
	}
	
	private String getDefault(final CharSequence s) {
		final int hashCode= computeHash(s);
		final int i1= (hashCode & HASHSET_MASK);
		final String s1= this.values[i1];
		if (s1 != null && s1.hashCode() == hashCode && s1.contentEquals(s)) {
//			fStatFound++;
			return s1;
		}
//		fStatSet++;
		return (this.values[i1]= s.toString().intern());
	}
	
	private String getDefault(final String s) {
		final int hashCode= s.hashCode();
		final int i1= (hashCode & HASHSET_MASK);
		final String s1= this.values[i1];
		if (s1 != null && s1.hashCode() == hashCode && s1.equals(s)) {
//			fStatFound++;
			return s1;
		}
//		fStatSet++;
		return (this.values[i1]= s.intern());
	}
	
	
//	private long fStatCharmap= 0;
//	private long fStatFound= 0;
//	private long fStatSet= 0;
//	
//	@Override
//	public String toString() {
//		final double charmap= fStatCharmap;
//		final double set= fStatSet;
//		final double found= fStatFound;
//		final double sum= charmap+set+found;
//		return "StringCache stat: sum="+ sum +" char=" + charmap/sum + " found=" + found/sum + " set=" + set/sum; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
//	}
	
}
