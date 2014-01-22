/*******************************************************************************
 * Copyright (c) 2012, 2013 Original authors and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Original authors and others - initial API and implementation
 ******************************************************************************/
// ~
package org.eclipse.nebula.widgets.nattable.columnCategories.gui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import org.eclipse.nebula.widgets.nattable.columnCategories.ColumnCategoriesModel;
import org.eclipse.nebula.widgets.nattable.columnCategories.Node;
import org.eclipse.nebula.widgets.nattable.columnChooser.ColumnEntry;
import org.eclipse.nebula.widgets.nattable.util.ObjectCloner;


/**
 * Provides data to the tree viewer representation of Column categories.
 * Data is in the form of {@link Node} objects exposed from the {@link ColumnCategoriesModel}
 */
public class AvailableColumnCategoriesProvider implements ITreeContentProvider {

	private final ColumnCategoriesModel model;
	private List<String> hiddenIndexes = new ArrayList<String>();

	public AvailableColumnCategoriesProvider(ColumnCategoriesModel model) {
		this.model = (ColumnCategoriesModel) ObjectCloner.deepCopy(model);
	}

	/**
	 * Hide the given {@link ColumnEntry} (ies) i.e. do not show them in the viewer. 
	 * @param entriesToHide the entries to hide
	 */
	public void hideEntries(List<ColumnEntry> entriesToHide) {
		for (ColumnEntry hiddenColumnEntry : entriesToHide) {
			model.removeColumnIndex(hiddenColumnEntry.getIndex());
		}
	}

	public Object[] getChildren(Object parentElement) {
		return castToNode(parentElement).getChildren().toArray();
	}

	public Object getParent(Object element) {
		return castToNode(element).getParent();
	}

	public boolean hasChildren(Object element) {
		return castToNode(element).getNumberOfChildren() > 0;
	}

	public Object[] getElements(Object inputElement) {
		return (model.getRootCategory() != null) ?
				getFilteredChildren(model.getRootCategory().getChildren()).toArray() :
				new Object[0];
	}
	
	private List<Node> getFilteredChildren(List<Node> allChildren) {
		List<Node> children = new ArrayList<Node>(allChildren);
		for (Node child : allChildren) {
			if (hiddenIndexes.contains(child.getData())) {
				children.remove(child);
			}
		}
		return children;
	}

	private Node castToNode(Object element) {
		return (Node) element;
	}

	public void dispose() {
		// No op.
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// No op.
	}

}
