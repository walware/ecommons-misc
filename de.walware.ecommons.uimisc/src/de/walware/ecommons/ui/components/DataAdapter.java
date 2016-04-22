/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.list.IObservableList;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.ITreeContentProvider;


public class DataAdapter<TItem> {
	
	
	public static class ListAdapter<TItem> extends DataAdapter<TItem> {
		
		
		final IObservableCollection list;
		
		
		public ListAdapter(final IObservableCollection list, final IObservableValue defaultValue) {
			super(defaultValue);
			if (list == null) {
				throw new NullPointerException("list"); //$NON-NLS-1$
			}
			
			this.list= list;
		}
		
		
		@Override
		public Object getAddParent(final Object element) {
			return this.list;
		}
		
		@Override
		public Object getParent(final Object element) {
			return this.list;
		}
		
		@Override
		protected Collection<? super TItem> getContainerFor(final Object element) {
			return this.list;
		}
		
		@Override
		public boolean isMoveAllowed(final Object element, final int direction) {
			if (!super.isMoveAllowed(element, direction)) {
				return false;
			}
			if (this.list instanceof List) {
				final int oldIdx= ((List) this.list).indexOf(element);
				final int newIdx= oldIdx + direction;
				return (oldIdx >= 0 && newIdx >= 0 & newIdx < this.list.size());
			}
			return false;
		}
		
		@Override
		public void delete(final List<? extends Object> elements) {
			setDirty(true);
			deleteDefault(elements);
			
			this.list.removeAll(elements);
			
			deleteChecked(elements);
		}
		
		@Override
		public void move(final Object item, final int direction) {
			final int oldIdx= ((IObservableList) this.list).indexOf(item);
			final int newIdx= oldIdx + direction;
			if (oldIdx < 0 || newIdx < 0 || newIdx >= this.list.size()) {
				return;
			}
			moveByIdx(oldIdx, newIdx);
		}
		
		protected void moveByIdx(final int oldIdx, final int newIdx) {
			((IObservableList) this.list).move(oldIdx, newIdx);
		}
		
		@Override
		protected void changeDefault(final TItem oldItem, final TItem newItem) {
			if (oldItem == null) {
				if (this.defaultValue != null && this.list.isEmpty()) {
					this.defaultValue.setValue(newItem);
				}
				return;
			}
			super.changeDefault(oldItem, newItem);
		}
		
	}
	
	public static class TreeAdapter<TItem> extends DataAdapter<TItem> {
		
		
		private final ITreeContentProvider contentProvider;
		
		public TreeAdapter(final ITreeContentProvider contentProvider, final IObservableValue defaultValue) {
			super(defaultValue);
			if (contentProvider == null) {
				throw new NullPointerException("contentProvider"); //$NON-NLS-1$
			}
			
			this.contentProvider= contentProvider;
		}
		
		
		@Override
		public Object getAddParent(final Object element) {
			if (isContentItem(element)) {
				return getParent(element);
			}
			return element;
		}
		
		@Override
		public Object getParent(final Object element) {
			return this.contentProvider.getParent(element);
		}
		
		@Override
		public Object[] getChildren(final Object element) {
			return this.contentProvider.getChildren(element);
		}
		
	}
	
	
	private Set<TItem> checkedSet;
	
	protected final IObservableValue defaultValue;
	
	private boolean isDirty;
	
	
	public DataAdapter(final IObservableValue defaultValue) {
		this.defaultValue= defaultValue;
	}
	
	
	public Object getAddParent(final Object element) {
		return null;
	}
	
	public Object getParent(final Object element) {
		return null;
	}
	
	public Object[] getChildren(final Object element) {
		return null;
	}
	
	public boolean isAddAllowed(final Object element) {
		return true;
	}
	
	public boolean isContentItem(final Object element) {
		return true;
	}
	
	public boolean isModifyAllowed(final Object element) {
		return isContentItem(element);
	}
	
	public boolean isMoveAllowed(final Object element, final int direction) {
		return isModifyAllowed(element);
	}
	
	public boolean isDeleteAllowed(final Object element) {
		return isModifyAllowed(element);
	}
	
	public TItem getModelItem(final Object element) {
		return (TItem) element;
	}
	
	public Object getViewerElement(final TItem item, final Object parent) {
		return item;
	}
	
	
	public void setCheckedModel(final Set<TItem> set) {
		this.checkedSet= set;
	}
	
	protected Object getContainerFor(final Object element) {
		return null;
	}
	
	protected IObservableValue getDefaultFor(final TItem item) {
		return this.defaultValue;
	}
	
	public void setDefault(final TItem item) {
		final IObservableValue observable= getDefaultFor(item);
		if (observable == null) {
			return;
		}
		this.isDirty= true;
		if (item != null) {
			observable.setValue(getDefaultValue(item));
		}
	}
	
	public Object change(final TItem oldItem, final TItem newItem,
			final Object parent, final Object container ) {
		if (container instanceof Collection) {
			final Collection list= (Collection) container;
			setDirty(true);
			changeDefault(oldItem, newItem);
			if (oldItem == null) {
				list.add(newItem);
			}
			else {
				if (oldItem != newItem) { // can be directly manipulated or replaced)
					if (list instanceof List) {
						final int idx= ((List) list).indexOf(oldItem);
						((List) list).set(idx, newItem);
					}
					else {
						list.remove(oldItem);
						list.add(newItem);
					}
				}
			}
			final Object editElement= getViewerElement(newItem, parent);
			
			changeChecked(oldItem, newItem);
			return editElement;
		}
		else {
			throw new UnsupportedOperationException();
		}
	}
	
	protected Object getDefaultValue(final TItem item) {
		return item;
	}
	
	protected void changeDefault(final TItem oldItem, final TItem newItem) {
		if (oldItem == null) {
			return;
		}
		final IObservableValue observable= getDefaultFor(oldItem);
		if (observable == null) {
			return;
		}
		final Object oldValue= getDefaultValue(oldItem);
		final Object newValue= getDefaultValue(newItem);
		if (oldValue != newValue && oldValue.equals(observable.getValue())) {
			observable.setValue(newValue);
		}
	}
	
	protected void changeChecked(final TItem oldItem, final TItem newItem) {
		if (this.checkedSet != null) {
			if (oldItem == null) {
				this.checkedSet.add(newItem);
			}
			else {
				if (this.checkedSet.remove(oldItem)) {
					this.checkedSet.add(newItem);
				}
			}
		}
	}
	
	public void delete(final List<? extends Object> elements) {
		setDirty(true);
		deleteDefault(elements);
		
		for (final Object element : elements) {
			delete(getModelItem(element), getContainerFor(element));
		}
		
		deleteChecked(elements);
	}
	
	protected void delete(final TItem item, final Object container) {
		if (container instanceof Collection) {
			((Collection) container).remove(item);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}
	
	protected void deleteDefault(final List<? extends Object> elements) {
		if (elements.isEmpty()) {
			return;
		}
		for (final Object element : elements) {
			final TItem item= getModelItem(element);
			if (item == null) {
				continue;
			}
			final IObservableValue observable= getDefaultFor(item);
			if (observable == null) {
				continue;
			}
			final Object itemValue= getDefaultValue(item);
			if (itemValue != null && itemValue.equals(observable.getValue())) {
				observable.setValue(null);
				return;
			}
		}
	}
	
	protected void deleteChecked(final List<? extends Object> elements) {
		if (this.checkedSet != null) {
			this.checkedSet.removeAll(elements);
		}
	}
	
	public void move(final Object item, final int direction) {
		throw new UnsupportedOperationException();
	}
	
	
	public void setDirty(final boolean isDirty) {
		this.isDirty= isDirty;
	}
	
	public boolean isDirty() {
		return this.isDirty;
	}
	
}
