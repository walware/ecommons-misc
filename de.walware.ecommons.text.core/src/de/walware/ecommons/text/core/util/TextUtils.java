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

package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension3;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;

import de.walware.ecommons.text.core.sections.IDocContentSections;


/**
 * @since de.walware.ecommons.text 1.0
 */
public final class TextUtils {
	
	
	public static Object getLockObject(final Object o) {
		final Object lockObject= (o instanceof ISynchronizable) ?
				((ISynchronizable) o).getLockObject() : null;
		return (lockObject != null) ? lockObject : o;
	}
	
	
	/**
	 * @since de.walware.ecommons.text 1.1
	 */
	public static String getContentType(final IDocument document, final IDocContentSections contentInfo,
			final int offset, final boolean preferOpenPartition)
			throws BadPartitioningException, BadLocationException {
		return ((IDocumentExtension3) document).getContentType(contentInfo.getPartitioning(),
				offset, preferOpenPartition );
	}
	
	/**
	 * @since de.walware.ecommons.text 1.1
	 */
	public static ITypedRegion getPartition(final IDocument document, final IDocContentSections contentInfo,
			final int offset, final boolean preferOpenPartition)
					throws BadPartitioningException, BadLocationException {
		return ((IDocumentExtension3) document).getPartition(contentInfo.getPartitioning(),
				offset, preferOpenPartition );
	}
	
	
}
