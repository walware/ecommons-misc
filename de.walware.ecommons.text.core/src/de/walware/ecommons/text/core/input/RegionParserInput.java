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

import org.eclipse.jface.text.IRegion;

import de.walware.ecommons.collections.ImList;


/**
 * Text parser input limiting the source to the specified regions.
 */
public class RegionParserInput extends FilterParserInput {
	
	
	private ImList<? extends IRegion> regions;
	private int regionIdx;
	
	private String regionSeparator;
	
	
	public RegionParserInput(final TextParserInput source, final ImList<? extends IRegion> regions,
			final int defaultBufferSize) {
		super(source, defaultBufferSize);
		
		this.regions= regions;
		this.regionIdx= -1;
	}
	
	public RegionParserInput(final TextParserInput source, final ImList<? extends IRegion> regions) {
		this(source, regions, DEFAULT_BUFFER_SIZE);
	}
	
	public RegionParserInput(final String source, final ImList<? extends IRegion> regions) {
		this(new StringParserInput(source).init(), regions,
				Math.min(source.length() + regions.size(), DEFAULT_BUFFER_SIZE) );
	}
	
	
	public void setSeparator(final String separator) {
		this.regionSeparator= separator;
	}
	
	public RegionParserInput reset(final ImList<? extends IRegion> regions) {
		if (regions == null) {
			throw new NullPointerException("regions"); //$NON-NLS-1$
		}
		this.regions= regions;
		this.regionIdx= -1;
		
		super.reset();
		
		return this;
	}
	
	@Override
	public RegionParserInput init() {
		super.init();
		
		return this;
	}
	
	@Override
	public RegionParserInput init(final int startIndex, final int stopIndex) {
		this.regionIdx= -1;
		
		super.init(startIndex, stopIndex);
		
		return this;
	}
	
	
	@Override
	protected int read(final TextParserInput in, final char[] buffer,
			final int[] beginIndexes, final int[] endIndexes,
			final int beginIdx, final int requiredEnd, final int recommendEnd) {
		if (this.regionIdx < 0) {
			this.regionIdx= 0;
			if (!nextRegion(in)) {
				return 0;
			}
		}
		int idx= beginIdx;
		ITER_C0: while (idx < recommendEnd) {
			final int c0= in.get(0);
			final int index= in.getIndex();
			if (c0 == EOF) {
				if (nextRegion(in)) {
					if (this.regionSeparator != null) {
						for (int i= 1; i < this.regionSeparator.length(); i++, idx++) {
							buffer[idx]= this.regionSeparator.charAt(i);
							beginIndexes[idx]= index;
							endIndexes[idx]= index;
						}
					}
					continue ITER_C0;
				}
				break ITER_C0;
			}
			
			buffer[idx]= (char) c0;
			beginIndexes[idx]= index;
			endIndexes[idx]= index + in.getLengthInSource(1);
			in.consume(1);
			idx++;
			continue ITER_C0;
		}
		beginIndexes[idx]= in.getIndex();
		return idx;
	}
	
	private boolean nextRegion(final TextParserInput source) {
		while (this.regionIdx < this.regions.size()) {
			final IRegion region= this.regions.get(this.regionIdx++);
			final int start= Math.max(region.getOffset(), getStartIndex());
			final int end= Math.min(region.getOffset() + region.getLength(), getStopIndex());
			if (start < end) {
				source.init(start, end);
				return true;
			}
		}
		return false;
	}
	
}
