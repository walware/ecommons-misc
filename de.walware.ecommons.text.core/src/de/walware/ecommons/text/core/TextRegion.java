/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core;

import org.eclipse.jface.text.IRegion;


public class TextRegion implements ITextRegion {
	
	
	private final int beginOffset;
	private final int endOffset;
	
	
	/**
	 * @param beginOffset the beginning offset, inclusive
	 * @param endOffset the ending offset, exclusive
	 */
	public TextRegion(final int beginOffset, final int endOffset) {
		if (beginOffset > endOffset) {
			throw new IllegalArgumentException("beginOffset > endOffset: beginOffset= " + beginOffset + ", endOffset= " + endOffset); //$NON-NLS-1$ //$NON-NLS-2$
		}
		this.beginOffset= beginOffset;
		this.endOffset= endOffset;
	}
	
	public TextRegion(final ITextRegion region) {
		this(region.getOffset(), region.getEndOffset());
	}
	
	public TextRegion(final IRegion region) {
		this(region.getOffset(), region.getLength() - region.getOffset());
	}
	
	
	@Override
	public final int getOffset() {
		return this.beginOffset;
	}
	
	@Override
	public final int getEndOffset() {
		return this.endOffset;
	}
	
	@Override
	public final int getLength() {
		return this.endOffset - this.beginOffset;
	}
	
	
	@Override
	public int hashCode() {
		return (this.beginOffset << 24) | (getLength() << 16);
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj instanceof IRegion) {
			final IRegion other= (IRegion) obj;
			return (this.beginOffset == other.getOffset()
					&& getLength() == other.getLength());
		}
		return false;
	}
	
	
	protected final void appendIntervalString(final StringBuilder sb) {
		sb.append('[');
		sb.append(this.beginOffset);
		sb.append(',');
		sb.append(this.endOffset);
		sb.append(')');
	}
	
	@Override
	public String toString() {
		final StringBuilder sb= new StringBuilder();
		appendIntervalString(sb);
		return sb.toString();
	}
	
}
