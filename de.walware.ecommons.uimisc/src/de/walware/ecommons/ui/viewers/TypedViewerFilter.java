/*=============================================================================#
 # Copyright (c) 2000-2016 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.viewers;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import de.walware.jcommons.collections.ImCollection;


/**
 * Viewer filter used in selection dialogs.
 */
public class TypedViewerFilter extends ViewerFilter {
	
	
	private final ImCollection<Class<?>> acceptedTypes;
	private final ImCollection<Object> rejectedElements;
	
	
	/**
	 * Creates a filter that only allows elements of gives types.
	 * 
	 * @param acceptedTypes The types of accepted elements
	 */
	public TypedViewerFilter(final ImCollection<Class<?>> acceptedTypes) {
		this(acceptedTypes, null);
	}
	
	/**
	 * Creates a filter that only allows elements of gives types, but not from a
	 * list of rejected elements.
	 * 
	 * @param acceptedTypes Accepted elements must be of this types
	 * @param rejectedElements Element equals to the rejected elements are filtered out
	 */
	public TypedViewerFilter(final ImCollection<Class<?>> acceptedTypes,
			final ImCollection<Object> rejectedElements) {
		if (acceptedTypes == null) {
			throw new NullPointerException("acceptedTypes"); //$NON-NLS-1$
		}
		this.acceptedTypes= acceptedTypes;
		this.rejectedElements= rejectedElements;
	}
	
	
	@Override
	public boolean select(final Viewer viewer, final Object parentElement, final Object element) {
		if (this.rejectedElements != null && this.rejectedElements.contains(element)) {
			return false;
		}
		for (final Class<?> acceptedType : this.acceptedTypes) {
			if (acceptedType.isInstance(element)) {
				return true;
			}
		}
		return false;
	}
	
}
