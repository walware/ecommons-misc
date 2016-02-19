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

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;


/**
 * Text parser input for JFace document.
 */
public class DocumentParserInput extends TextParserInput {
	
	
	private IDocument document;
	
	
	public DocumentParserInput(final int defaultBufferSize) {
		super(defaultBufferSize);
	}
	
	public DocumentParserInput() {
		this(DEFAULT_BUFFER_SIZE);
	}
	
	public DocumentParserInput(final IDocument document) {
		this(Math.min(document.getLength(), DEFAULT_BUFFER_SIZE));
		
		this.document= document;
	}
	
	
	public DocumentParserInput reset(final IDocument document) {
		if (document == null) {
			throw new NullPointerException("document"); //$NON-NLS-1$
		}
		this.document= document;
		
		super.reset();
		
		return this;
	}
	
	
	public IDocument getDocument() {
		return this.document;
	}
	
	@Override
	protected int getSourceLength() {
		return (this.document != null) ? this.document.getLength() : 0;
	}
	
	@Override
	protected String getSourceString() {
		return (this.document != null) ? this.document.get() : null;
	}
	
	
	@Override
	protected void doUpdateBuffer(final int index, final char[] buffer,
			final int requiredLength, final int recommendLength) {
		try {
			final int length= Math.min(recommendLength, getStopIndex() - index);
			final int reused= copyBuffer0(buffer);
			if (length > reused) {
				final int l= length - reused;
				this.document.get(index + reused, l).getChars(0, l, buffer, reused);
			}
			setBuffer(buffer, 0, length);
		}
		catch (final BadLocationException e) {
			throw new RuntimeException(e);
		}
	}
	
}
