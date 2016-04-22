/*=============================================================================#
 # Copyright (c) 2010-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.viewers;

import java.util.Arrays;
import java.util.Comparator;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;


/**
 * Viewer Comparator using a common comparator to compare the elements.
 */
public class ComparatorViewerComparator extends ViewerComparator {
	
	
	private final Comparator comparator;
	
	
	public ComparatorViewerComparator(final Comparator comparator) {
		this.comparator= comparator;
	}
	
	
	@Override
	public int compare(final Viewer viewer, final Object e1, final Object e2) {
		return this.comparator.compare(e1, e2);
	}
	
	@Override
	public void sort(final Viewer viewer, final Object[] elements) {
		Arrays.sort(elements, this.comparator);
	}
	
}
