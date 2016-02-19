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

import java.util.IdentityHashMap;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentPartitioningChangedEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IDocumentPartitioningListenerExtension2;
import org.eclipse.jface.text.Position;

import de.walware.jcommons.collections.CopyOnWriteIdentityListSet;

import de.walware.ecommons.text.core.util.TextUtils;


public class DocumentEnhancer {
	
	
	private static class EnhancementImpl extends Position
			implements IDocumentEnhancement, IDocumentPartitioningListener, IDocumentPartitioningListenerExtension2 {
		
		
		private final CopyOnWriteIdentityListSet<IDocumentPartitioningListenerExtension2> partitioningListeners= new CopyOnWriteIdentityListSet<>();
		
		private IdentityHashMap<String, Object> data;
		
		
		@Override
		public void addPrePartitioningListener(final IDocumentPartitioningListenerExtension2 listener) {
			this.partitioningListeners.add(listener);
		}
		
		@Override
		public void removePrePartitioningListener(final IDocumentPartitioningListenerExtension2 listener) {
			this.partitioningListeners.remove(listener);
		}
		
		
		@Override
		public void documentPartitioningChanged(final IDocument document) {
		}
		
		@Override
		public void documentPartitioningChanged(final DocumentPartitioningChangedEvent event) {
			for (final IDocumentPartitioningListenerExtension2 listener : this.partitioningListeners) {
				listener.documentPartitioningChanged(event);
			}
		}
		
		
		@Override
		public Object getData(final String key) {
			return (this.data != null) ? this.data.get(key) : null;
		}
		
		@Override
		public void setData(final String key, final Object value) {
			if (this.data == null) {
				this.data= new IdentityHashMap<>();
			}
			this.data.put(key, value);
		}
		
	}
	
	
	private static final String POSITION_CATEGORY= "de.walware.ecommons.text.DocumentEnhancement"; //$NON-NLS-1$
	
	
	private static EnhancementImpl doSetup(final IDocument document) throws BadLocationException, BadPositionCategoryException {
		document.addPositionCategory(POSITION_CATEGORY);
		
		final EnhancementImpl documentEnh= new EnhancementImpl();
		document.addPosition(POSITION_CATEGORY, documentEnh);
		document.addDocumentPartitioningListener(documentEnh);
		
		return documentEnh;
	}
	
	public static void setup(final IDocument document) {
		synchronized (TextUtils.getLockObject(document)) {
			try {
				if (!document.containsPositionCategory(POSITION_CATEGORY)) {
					doSetup(document);
				}
			}
			catch (final BadPositionCategoryException | BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	public static IDocumentEnhancement get(final IDocument document) {
		synchronized (TextUtils.getLockObject(document)) {
			try {
				if (document.containsPositionCategory(POSITION_CATEGORY)) {
					return (EnhancementImpl) document.getPositions(POSITION_CATEGORY)[0];
				}
				return doSetup(document);
			}
			catch (final BadPositionCategoryException | BadLocationException e) {
				throw new RuntimeException(e);
			}
		}
	}
	
}
