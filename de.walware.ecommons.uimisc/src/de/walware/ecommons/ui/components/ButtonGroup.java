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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.viewers.AbstractTableViewer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.UIAccess;
import de.walware.ecommons.ui.util.ViewerUtil;


/**
 * Composite with buttons to manipulate list or tree items.
 */
public class ButtonGroup<TItem> extends Composite {
	
	
	public static final int ADD_NEW=                        1 << 0;
	public static final int ADD_COPY=                       1 << 1;
	public static final int ADD_ANY=                        ADD_NEW | ADD_COPY;
	public static final int EDIT=                           1 << 2;
	
	
	public static interface IActions<ItemType> {
		
		
		ItemType edit(final int command, final ItemType item, final Object parent);
		
		void updateState(IStructuredSelection selection);
		
	}
	
	public static interface IImportExportActions<ItemType> extends IActions<ItemType> {
		
		void importItems();
		
		void exportItems(final List<? extends Object> items);
		
	}
	
	
	public static class SelectionHandler<TItem> extends SelectionAdapter implements DisposeListener {
		
		
		private ButtonGroup<TItem> group;
		
		private Control control;
		
		
		public void update(final IStructuredSelection selection) {
			setEnabled(getElement(selection) != null);
		}
		
		protected ButtonGroup<TItem> getGroup() {
			return this.group;
		}
		
		protected Control getControl() {
			return this.control;
		}
		
		protected void setEnabled(final boolean enabled) {
			this.control.setEnabled(enabled);
		}
		
		protected Object getElement(final IStructuredSelection selection) {
			if (selection.size() == 1) {
				return selection.getFirstElement();
			}
			return null;
		}
		
		@Override
		public void widgetSelected(final SelectionEvent e) {
			run((IStructuredSelection) this.group.viewer.getSelection());
		}
		
		public boolean run(final IStructuredSelection selection) {
			return false;
		}
		
		@Override
		public void widgetDisposed(final DisposeEvent e) {
		}
		
	}
	
	public static class ElementListHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			final List<? extends Object> list= getElement(selection);
			setEnabled(list != null && !list.isEmpty());
		}
		
		@Override
		protected List<? extends Object> getElement(final IStructuredSelection selection) {
			return selection.toList();
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			return false;
		}
		
	}
	
	public static class ItemListHandler<TItem> extends ElementListHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			setEnabled(hasItem(selection.toList()));
		}
		
		@Override
		protected List<TItem> getElement(final IStructuredSelection selection) {
			return getItems(selection.toList());
		}
		
		
		protected boolean hasItem(final List<? extends Object> list) {
			final DataAdapter<?> dataAdapter= getGroup().getDataAdapter();
			for (final Object element : list) {
				if (dataAdapter.isContentItem(element)) {
					return true;
				}
				if (getGroup().treeMode) {
					if (hasItem(dataAdapter.getChildren(element))) {
						return true;
					}
				}
			}
			return false;
		}
		
		protected boolean hasItem(final Object[] array) {
			if (array != null) {
				final DataAdapter<?> dataAdapter= getGroup().getDataAdapter();
				for (final Object element : array) {
					if (dataAdapter.isContentItem(element)) {
						return true;
					}
					if (hasItem(dataAdapter.getChildren(element))) {
						return true;
					}
				}
			}
			return false;
		}
		
		protected List<TItem> getItems(final List<? extends Object> list) {
			if (!list.isEmpty()) {
				final DataAdapter<TItem> dataAdapter= getGroup().getDataAdapter();
				final List<TItem> items= new ArrayList<>();
				for (final Object element : list) {
					if (dataAdapter.isContentItem(element)) {
						items.add(dataAdapter.getModelItem(element));
					}
					else if (getGroup().treeMode) {
						collectItems(dataAdapter.getChildren(element), items);
					}
				}
				return items;
			}
			return null;
		}
		
		protected void collectItems(final Object[] elements, final List<TItem> items) {
			if (elements != null) {
				final DataAdapter<TItem> dataAdapter= getGroup().getDataAdapter();
				for (final Object element : elements) {
					if (dataAdapter.isContentItem(element)) {
						items.add(dataAdapter.getModelItem(element));
					}
					else {
						collectItems(dataAdapter.getChildren(element), items);
					}
				}
			}
		}
		
	}
	
	public static class AddHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
			if (getGroup().treeMode) {
				final Object element= getElement(selection);
				setEnabled(element != null
						&& getGroup().getDataAdapter().isAddAllowed(selection.getFirstElement()) );
			}
			else {
				setEnabled(true);
			}
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			getGroup().editElement(ADD_NEW, getElement(selection));
			return true;
		}
		
	}
	
	public static class CopyHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element= super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isModifyAllowed(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element= getElement(selection);
			if (element != null) {
				getGroup().editElement(ADD_COPY, element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class EditHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element= super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isModifyAllowed(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element= getElement(selection);
			if (element != null) {
				getGroup().editElement(EDIT, element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class DeleteHandler extends ElementListHandler {
		
		@Override
		protected List<? extends Object> getElement(final IStructuredSelection selection) {
			final List<? extends Object> list= super.getElement(selection);
			final DataAdapter<?> adapter= getGroup().getDataAdapter();
			for (final Object object : list) {
				if (!adapter.isDeleteAllowed(object)) {
					return null;
				}
			}
			return list;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final List<? extends Object> list= getElement(selection);
			if (list != null) {
				getGroup().delete0(list);
				return true;
			}
			return false;
		}
		
	}
	
	public static class DefaultHandler extends SelectionHandler {
		
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element= super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isContentItem(element)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element= getElement(selection);
			if (element != null) {
				getGroup().setDefault(element);
				return true;
			}
			return false;
		}
		
	}
	
	public static class MoveHandler extends SelectionHandler {
		
		
		private final int fDirection;
		
		
		public MoveHandler(final int direction) {
			this.fDirection= direction;
		}
		
		
		protected int getDirection() {
			return this.fDirection;
		}
		
		@Override
		protected Object getElement(final IStructuredSelection selection) {
			final Object element= super.getElement(selection);
			return (element != null && getGroup().getDataAdapter().isMoveAllowed(element, this.fDirection)) ?
					element : null;
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final Object element= getElement(selection);
			if (element != null) {
				getGroup().move0(element, this.fDirection);
				return true;
			}
			return false;
		}
		
	}
	
	public static class ImportHandler extends SelectionHandler {
		
		
		@Override
		public void update(final IStructuredSelection selection) {
		}
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			getGroup().import0();
			return true;
		}
		
	}
	
	public static class ExportHandler<TItem> extends ItemListHandler<TItem> {
		
		
		@Override
		public boolean run(final IStructuredSelection selection) {
			final List<TItem> list= getElement(selection);
			if (list != null) {
				getGroup().export0(list);
				return true;
			}
			return false;
		}
		
	}
	
	
	private static final Object FIRST_ELEMENT= new Object();
	
	
	private DataAdapter<TItem> dataAdapter;
	
	private IActions<TItem> actions;
	
	private StructuredViewer viewer;
	private boolean treeMode;
	private boolean cellMode;
	
	private SelectionHandler<?> editHandler;
	private SelectionHandler<?> deleteHandler;
	
	private final List<SelectionHandler<?>> handlers= new ArrayList<>();
	
	private int fCachedWidthHint;
	
	
	public ButtonGroup(final Composite parent) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.createCompositeGrid(1));
	}
	
	public ButtonGroup(final Composite parent, final IActions<TItem> actions, final boolean cellMode) {
		super(parent, SWT.NONE);
		setLayout(LayoutUtil.createCompositeGrid(1));
		this.actions= actions;
		this.cellMode= cellMode;
	}
	
	
	public DataAdapter<TItem> getDataAdapter() {
		return this.dataAdapter;
	}
	
	protected void addLayoutData(final Control control) {
		if (this.fCachedWidthHint == 0 && control instanceof Button) {
			this.fCachedWidthHint= LayoutUtil.hintWidth((Button) control);
		}
		final GridData gd= new GridData(SWT.FILL, SWT.FILL, true, false);
		gd.widthHint= this.fCachedWidthHint;
		control.setLayoutData(gd);
	}
	
	public void add(final Control control, final SelectionHandler<TItem> handler) {
		handler.group= this;
		handler.control= control;
		addLayoutData(control);
		
		control.addDisposeListener(handler);
		if (control instanceof Button) {
			((Button) control).addSelectionListener(handler);
		}
		
		this.handlers.add(handler);
	}
	
	public void addAddButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		String label= SharedMessages.CollectionEditing_AddItem_label;
		if (!this.cellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler= new AddHandler();
		}
		add(button, handler);
	}
	
	public void addCopyButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		String label= SharedMessages.CollectionEditing_CopyItem_label;
		if (!this.cellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler= new CopyHandler();
		}
		add(button, handler);
	}
	
	public void addEditButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		String label= SharedMessages.CollectionEditing_EditItem_label;
		if (!this.cellMode) {
			label += "..."; //$NON-NLS-1$
		}
		button.setText(label);
		if (handler == null) {
			handler= new EditHandler();
		}
		this.editHandler= handler;
		add(button, handler);
	}
	
	public void addDeleteButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_RemoveItem_label);
		if (handler == null) {
			handler= new DeleteHandler();
		}
		this.deleteHandler= handler;
		add(button, handler);
	}
	
	public void addDefaultButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_DefaultItem_label);
		if (handler == null) {
			handler= new DefaultHandler();
		}
		add(button, handler);
	}
	
	public void addUpButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_MoveItemUp_label);
		if (handler == null) {
			handler= new MoveHandler(-1);
		}
		add(button, handler);
	}
	
	public void addDownButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_MoveItemDown_label);
		if (handler == null) {
			handler= new MoveHandler(1);
		}
		add(button, handler);
	}
	
	public void addImportButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_Import_label);
		if (handler == null) {
			handler= new ImportHandler();
		}
		add(button, handler);
	}
	
	public void addExportButton(SelectionHandler<TItem> handler) {
		final Button button= new Button(this, SWT.PUSH);
		button.setText(SharedMessages.CollectionEditing_Export_label);
		if (handler == null) {
			handler= new ExportHandler();
		}
		add(button, handler);
	}
	
	
	public void addSeparator() {
		LayoutUtil.addSmallFiller(this, false);
	}
	
	public void connectTo(final StructuredViewer viewer, final IObservableCollection list,
			final IObservableValue defaultValue) {
		if (list != null) {
			connectTo(viewer, new DataAdapter.ListAdapter<TItem>(list, defaultValue));
		}
		else if (viewer.getContentProvider() instanceof ITreeContentProvider) {
			connectTo(viewer, new DataAdapter.TreeAdapter<TItem>(
					(ITreeContentProvider) viewer.getContentProvider(), defaultValue ));
		}
		else {
			connectTo(viewer, new DataAdapter<TItem>(defaultValue));
		}
	}
	
	public void connectTo(final StructuredViewer viewer, final DataAdapter<TItem> adapter) {
		this.viewer= viewer;
		this.treeMode= (viewer instanceof AbstractTreeViewer);
		if (this.deleteHandler != null) {
			this.viewer.getControl().addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(final KeyEvent event) {
					if (event.character == SWT.DEL && event.stateMask == 0 && ButtonGroup.this.deleteHandler != null) {
						ButtonGroup.this.deleteHandler.run((IStructuredSelection) ButtonGroup.this.viewer.getSelection());
					}
				}	
			});
		}
		if (this.editHandler != null && !this.cellMode) {
			this.viewer.addDoubleClickListener(new IDoubleClickListener() {
				@Override
				public void doubleClick(final DoubleClickEvent event) {
					final IStructuredSelection selection= (IStructuredSelection) event.getSelection();
					if (ButtonGroup.this.editHandler != null && !ButtonGroup.this.editHandler.run(selection)
							&& ButtonGroup.this.treeMode && selection.size() == 1) {
						((AbstractTreeViewer) ButtonGroup.this.viewer).setExpandedState(selection.getFirstElement(), 
								!((AbstractTreeViewer) ButtonGroup.this.viewer).getExpandedState(selection.getFirstElement()));
					}
				}
			});
		}
		this.viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(final SelectionChangedEvent event) {
				updateState();
			}
		});
		
		this.dataAdapter= adapter;
	}
	
	public void setCheckedModel(final Set<TItem> set) {
		this.dataAdapter.setCheckedModel(set);
	}
	
	public void updateState() {
		final IStructuredSelection selection= (IStructuredSelection) this.viewer.getSelection();
		
		for (final SelectionHandler<?> handler : this.handlers) {
			handler.update(selection);
		}
		
		if (this.actions != null) {
			this.actions.updateState(selection);
		}
	}
	
	
	public void editElement(final int command, Object element) {
		final TItem orgItem= ((command & ADD_NEW) == 0 && element != null) ? this.dataAdapter.getModelItem(element) : null;
		final Object parent= ((command & ADD_NEW) != 0) ? this.dataAdapter.getAddParent(element) : this.dataAdapter.getParent(element);
		
		final TItem editItem= (this.actions != null) ?
				this.actions.edit(command, ((command & ADD_NEW) == 0) ? orgItem : null, parent) :
				edit1(((command & ADD_NEW) == 0) ? orgItem : null, (command & (ADD_NEW | ADD_COPY)) != 0, parent);
		if (editItem == null) {
			return;
		}
		element= this.dataAdapter.change(((command & ADD_ANY) == 0) ? orgItem : null, editItem, 
				parent, this.dataAdapter.getContainerFor(element) );
		refresh0(element, null);
		if (/*fCellMode &&*/ this.viewer instanceof ColumnViewer) {
			((ColumnViewer) this.viewer).editElement(element, 0);
		}
	}
	
	public void apply(final TItem oldItem, final TItem newItem) {
		if (newItem == null) {
			delete0(Collections.singletonList(oldItem));
		}
		else {
			final Object element= this.dataAdapter.change(oldItem, newItem, null,
					this.dataAdapter.getContainerFor(this.dataAdapter.getViewerElement(
							(oldItem != null) ? oldItem : newItem, null )));
			refresh0(element, null);
		}
	}
	
	public void deleteElements(final int command, final List<? extends Object> elements) {
		delete0(elements);
	}
	
	public void setDefault(final Object element) {
		setDefault0(element);
	}
	
	/**
	 * @deprecated implement {@link IActions#edit(int, Object, Object)}
	 */
	@Deprecated
	protected TItem edit1(final TItem item, final boolean newItem, final Object parent) {
		return null;
	}
	
	private void delete0(final List<? extends Object> elements) {
		if (elements.isEmpty()) {
			return;
		}
		
		final Object elementToSelect= getBestNeighbour(elements);
		
		this.dataAdapter.delete(elements);
		refresh0(null, elementToSelect);
	}
	
	private Object getBestNeighbour(final List<? extends Object> elements) {
		final Object parent= this.dataAdapter.getParent(elements.get(elements.size() - 1));
		final ImList<TItem> neighbours;
		{	Object[] array= null;
			if (parent != null) {
				array= this.dataAdapter.getChildren(parent);
			}
			if (array == null) {
				final Object container= this.dataAdapter.getContainerFor(elements.get(elements.size() - 1));
				if (container instanceof Collection) {
					array= ((Collection<?>) container).toArray();
				}
			}
			if (array == null) {
				return null;
			}
			
			{	final ViewerComparator comparator= this.viewer.getComparator();
				if (comparator != null) {
					comparator.sort(this.viewer, array);
				}
			}
			
			neighbours= ImCollections.newList((TItem[]) array);
		}
		
		{	int idx= neighbours.indexOf(elements.get(elements.size() - 1));
			if (idx >= 0) {
				int i= idx + 1;
				// forward
				for (; i < neighbours.size(); i++) {
					if (!elements.contains(neighbours.get(i))) {
						break;
					}
				}
				if (i == neighbours.size()) {
					// backward
					i= idx - 1;
					for (; i >= 0; i--) {
						if (!elements.contains(neighbours.get(i))) {
							break;
						}
					}
				}
				idx= i;
			}
			if (idx >= 0) {
				return neighbours.get(idx);
			}
			else {
				return parent;
			}
		}
	}
	
	private void setDefault0(final Object element) {
		final TItem item= this.dataAdapter.getModelItem(element);
		this.dataAdapter.setDefault(item);
		refresh0(null, null);
	}
	
	private void move0(final Object element, final int direction) {
		this.dataAdapter.move(element, direction);
		refresh0(element, null);
	}
	
	private void import0() {
		((IImportExportActions<?>) this.actions).importItems();
		refresh0(null, null);
	}
	
	private void export0(final List<? extends Object> items) {
		if (items == null || items.isEmpty()) {
			return;
		}
		((IImportExportActions<?>) this.actions).exportItems(items);
	}
	
	public void refresh() {
		refresh0(null, FIRST_ELEMENT);
	}
	
	public void refresh(final TItem elementToSelect) {
		refresh0(elementToSelect, null);
	}
	
	private void refresh0(final Object elementToSelect, Object elementToSelect2) {
		refresh1();
		if (elementToSelect != null || elementToSelect2 != null) {
//			Display.getCurrent().asyncExec(new Runnable() {
//				public void run() {
					if (UIAccess.isOkToUse(this.viewer)) {
						if (elementToSelect != null) {
							select(elementToSelect);
						}
						if (elementToSelect2 != null && this.viewer.getSelection().isEmpty()) {
							if (elementToSelect2 == FIRST_ELEMENT) {
								if (this.viewer instanceof AbstractTableViewer) {
									elementToSelect2= ((AbstractTableViewer) this.viewer).getElementAt(0);
								}
								else {
									elementToSelect2= null;
								}
							}
							if (elementToSelect2 != null) {
								select(elementToSelect2);
							}
						}
					}
//				}
//			});
		}
		updateState();
	}
	
	private void select(final Object element) {
		if (this.treeMode) {
			ViewerUtil.expandToLevel((AbstractTreeViewer) this.viewer, element, 0);
		}
		this.viewer.setSelection(new StructuredSelection(element), true);
	}
	
	
	protected void refresh1() {
		this.viewer.refresh();
	}
	
}
