/*=============================================================================#
 # Copyright (c) 2011-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ITextStore;

import de.walware.jcommons.lang.Immutable;


/**
 * Immutable document.
 * 
 * @since de.walware.ecommons.text 1.1
 */
public class ImmutableDocument extends AbstractDocument implements Immutable {
	
	
	private static class StringTextStore implements ITextStore {
		
		private final String content;
		
		/**
		 * Creates a new string text store with the given content.
		 *
		 * @param content the content
		 */
		public StringTextStore(final String content) {
			if (content == null) {
				throw new NullPointerException();
			}
			this.content= content;
		}
		
		@Override
		public char get(final int offset) {
			return this.content.charAt(offset);
		}
		
		@Override
		public String get(final int offset, final int length) {
			return this.content.substring(offset, offset + length);
		}
		
		@Override
		public int getLength() {
			return this.content.length();
		}
		
		@Override
		public void replace(final int offset, final int length, final String text) {
		}
		
		@Override
		public void set(final String text) {
		}
		
	}
	
	
	/**
	 * Creates a new read-only document with the given content.
	 *
	 * @param content the content
	 * @param lineDelimiters the line delimiters
	 */
	public ImmutableDocument(final String content, final long timestamp) {
		super();
		setTextStore(new StringTextStore(content));
		setLineTracker(new DefaultLineTracker());
		completeInitialization();
		super.set(content, timestamp);
	}
	
	
	@Override
	public void set(final String text, final long modificationStamp) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void replace(final int pos, final int length, final String text)
			throws BadLocationException {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void replace(final int pos, final int length, final String text,
			final long modificationStamp) throws BadLocationException {
		throw new UnsupportedOperationException();
	}
	
	
}
