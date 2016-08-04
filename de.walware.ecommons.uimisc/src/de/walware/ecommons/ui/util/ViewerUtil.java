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

package de.walware.ecommons.ui.util;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.util.Policy;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellNavigationStrategy;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckable;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.viewers.ViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import de.walware.jcommons.collections.CopyOnWriteIdentityListSet;
import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.components.SearchText;


/**
 * Utility class for JFace viewers
 */
public class ViewerUtil {
	
	public static class Node {
		
		private final String name;
		
		private Node parent;
		private final Node[] children;
		
		public Node(final String name, final Node[] children) {
			this.name= name;
			this.children= children;
			if (this.children != null) {
				for (final Node node : this.children) {
					node.parent= this;
				}
			}
		}
		
		public String getName() {
			return this.name;
		}
		
		public Node[] getChildren() {
			return this.children;
		}
		
	}
	
	
	public static class NodeContentProvider implements ITreeContentProvider {
		
		@Override
		public Object[] getElements(final Object inputElement) {
			return ((Node[]) inputElement);
		}
		
		@Override
		public Object getParent(final Object element) {
			return ((Node) element).parent;
		}
		
		@Override
		public boolean hasChildren(final Object element) {
			return ((Node) element).children != null;
		}
		
		@Override
		public Object[] getChildren(final Object parentElement) {
			return ((Node) parentElement).children;
		}
		
		@Override
		public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
		}
		
		@Override
		public void dispose() {
		}
		
	}
	
	public static Point calculateTreeSizeHint(final Control treeControl, final Node[] rootNodes, final int rows) {
		final Point pixels= new Point(0,0);
		final PixelConverter tool= new PixelConverter(treeControl);
		
		float factor= tool.convertWidthInCharsToPixels(2);
		final ScrollBar vBar= ((Scrollable) treeControl).getVerticalBar();
		if (vBar != null) {
			factor= vBar.getSize().x * 1.1f; // scrollbars and tree indentation guess
		}
		pixels.x= measureNodes(tool, factor, rootNodes, 1) + ((int) factor);
		pixels.y= tool.convertHeightInCharsToPixels(rows);
		
		return pixels;
	}
	
	/** recursive measure */
	private static int measureNodes(final PixelConverter tool, final float factor, final Node[] nodes, final int deepth) {
		int maxWidth= 0;
		for (final Node node : nodes) {
			maxWidth= Math.max(maxWidth, tool.convertWidthInCharsToPixels(node.name.length()) + (int) (deepth * factor));
			final Node[] children= node.getChildren();
			if (children != null) {
				maxWidth= Math.max(maxWidth, measureNodes(tool, factor * 0.95f, children, deepth+1));
			}
		}
		return maxWidth;
	}
	
	
	public static void addDoubleClickExpansion(final TreeViewer viewer) {
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(final DoubleClickEvent event) {
				final IStructuredSelection selection= (IStructuredSelection) event.getSelection();
				if (selection != null && selection.size() == 1) {
					final Object item= selection.getFirstElement();
					if (viewer.getExpandedState(item)) {
						viewer.collapseToLevel(item, TreeViewer.ALL_LEVELS);
					} else {
						viewer.expandToLevel(item, 1);
					}
				}
			}
		});
	}
	
	public static void setDefaultVisibleItemCount(final ComboViewer viewer) {
		final Control control= viewer.getControl();
		if (control instanceof Combo) {
			((Combo) control).setVisibleItemCount(25);
		}
		else if (control instanceof CCombo) {
			((CCombo) control).setVisibleItemCount(25);
		}
	}
	
	
	public static class TableComposite extends Composite {
		
		public TableViewer viewer;
		public Table table;
		public TableColumnLayout layout;
		
		public TableComposite(final Composite parent, final int tableStyle) {
			super(parent, SWT.NONE);
			
			this.layout= new TableColumnLayout();
			setLayout(this.layout);
			this.table= new Table(this, tableStyle);
			this.viewer= new TableViewer(this.table);
		}
		
		
		public TableViewerColumn addColumn(final String title, final int style, final ColumnLayoutData layoutData) {
			final TableViewerColumn column= new TableViewerColumn(this.viewer, style);
			if (title != null) {
				column.getColumn().setText(title);
			}
			this.layout.setColumnData(column.getColumn(), layoutData);
			return column;
		}
		
		public ViewerColumn getViewerColumn(final int index) {
			final TableColumn column= this.table.getColumn(index);
			if (column == null) {
				return null;
			}
			return (TableViewerColumn) column.getData(Policy.JFACE + ".columnViewer"); //$NON-NLS-1$
		}
		
	}
	
	public static class CheckboxTableComposite extends Composite {
		
		public CheckboxTableViewer viewer;
		public Table table;
		public TableColumnLayout layout;
		
		public CheckboxTableComposite(final Composite parent, final int tableStyle) {
			super(parent, SWT.NONE);
			
			this.layout= new TableColumnLayout();
			setLayout(this.layout);
			this.viewer= CheckboxTableViewer.newCheckList(this, tableStyle);
			this.table= this.viewer.getTable();
		}
		
		
		public TableViewerColumn addColumn(final String title, final int style, final ColumnLayoutData layoutData) {
			final TableViewerColumn column= new TableViewerColumn(this.viewer, style);
			column.getColumn().setText(title);
			this.layout.setColumnData(column.getColumn(), layoutData);
			return column;
		}
		
		public ViewerColumn getViewerColumn(final int index) {
			final TableColumn column= this.table.getColumn(index);
			if (column == null) {
				return null;
			}
			return (TableViewerColumn) column.getData(Policy.JFACE + ".columnViewer"); //$NON-NLS-1$
		}
		
	}
	
	public static class CheckboxColumnControl<TElement> extends ColumnLabelProvider implements Listener, ICheckable {
		
		private final TableViewer viewer;
		
		private final Collection<TElement> checkedElements;
		private final Collection<TElement> editableElements;
		
		private final CopyOnWriteIdentityListSet<ICheckStateListener> listeners= new CopyOnWriteIdentityListSet<>();
		
		
		public CheckboxColumnControl(final TableViewer viewer,
				final Set<TElement> checkedElements, final Collection<TElement> editableElements) {
			this.viewer= viewer;
			this.checkedElements= checkedElements;
			this.editableElements= editableElements;
		}
		
		
		private int indexOf(final TElement element) {
			final Object input= this.viewer.getInput();
			if (input instanceof List) {
				return ((List<?>) input).indexOf(element);
			}
			else {
				final Object[] array= (Object[]) input;
				for (int i= 0; i < array.length; i++) {
					if (array[i] == element) {
						return i;
					}
				}
				return -1;
			}
		}
		
		
		protected Collection<TElement> getCheckedElements() {
			return this.checkedElements;
		}
		
		@Override
		public boolean getChecked(final Object element) {
			return getCheckedElements().contains(element);
		}
		
		@Override
		public boolean setChecked(final Object element, final boolean state) {
			if (state) {
				getCheckedElements().add((TElement) element);
			}
			else {
				getCheckedElements().remove(element);
			}
			return true;
		}
		
		@Override
		public void addCheckStateListener(final ICheckStateListener listener) {
			this.listeners.add(listener);
		}
		
		@Override
		public void removeCheckStateListener(final ICheckStateListener listener) {
			this.listeners.remove(listener);
		}
		
		@Override
		public void update(final ViewerCell cell) {
			final Object element= cell.getElement();
			final TableItem item= (TableItem) cell.getItem();
			
			TableEditor editor= (TableEditor) item.getData("editor"); //$NON-NLS-1$
			final Button button;
			if (editor == null
					|| editor.getEditor() == null || editor.getEditor().isDisposed()) {
				button= new Button(this.viewer.getTable(), SWT.CHECK | SWT.NO_FOCUS);
				editor= new TableEditor(this.viewer.getTable());
				button.pack();
				final Point buttonSize= button.getSize();
				editor.horizontalAlignment= SWT.CENTER;
				editor.minimumWidth= buttonSize.x;
				editor.minimumHeight= buttonSize.y;
				editor.setEditor(button, item, 0);
				item.setData("editor", editor); //$NON-NLS-1$
				
				button.addListener(SWT.Selection, this);
				button.addListener(SWT.MouseDown, this);
				button.addListener(SWT.FocusIn, this);
				button.addListener(SWT.Activate, this);
			}
			else {
				button= (Button) editor.getEditor();
			}
			
			button.setData(element);
			
			button.setEnabled(this.editableElements.contains(element));
			button.setSelection(getChecked(element));
			editor.setEditor(button, item, cell.getColumnIndex());
		};
		
		public int hintColumnWidth() {
			final Button button= new Button(this.viewer.getTable(), SWT.CHECK);
			try {
				return button.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
			}
			finally {
				button.dispose();
			}
		}
		
		@Override
		public void handleEvent(final Event event) {
			final Button button= (Button) event.widget;
			final Object element= button.getData();
			
			switch (event.type) {
			
			case SWT.Selection:
				if (!this.editableElements.contains(element)) {
					event.doit= false;
					return;
				}
				doCheck(element, button.getSelection());
				return;
			case SWT.MouseDown:
				event.doit= false;
				if (!this.editableElements.contains(element)) {
					return;
				}
				this.viewer.setSelection(new StructuredSelection(element));
				button.setSelection(!button.getSelection());
				doCheck(element, button.getSelection());
				recheckSelection();
				return;
				
			case SWT.FocusIn:
				event.doit= false;
				this.viewer.getTable().forceFocus();
				return;
			}
		}
		
		protected void toggle(final TElement element) {
			final TableItem item= getItem(element);
			if (item != null) {
				final TableEditor editor= (TableEditor) item.getData("editor");
				final Button button= (Button) editor.getEditor();
				button.setSelection(!button.getSelection());
				doCheck(element, button.getSelection());
			}
			else {
				doCheck(element, !this.checkedElements.contains(element));
			}
		}
		
		@SuppressWarnings("unchecked")
		protected TElement getSelectedElement() {
			return (TElement) ((StructuredSelection) CheckboxColumnControl.this.viewer.getSelection()).getFirstElement();
		}
		
		private void doCheck(final Object element, final boolean state) {
			setChecked(element, state);
			final CheckStateChangedEvent event= new CheckStateChangedEvent(this, element, state);
			for (final ICheckStateListener listener : this.listeners) {
				listener.checkStateChanged(event);
			}
		}
		
		private TableItem getItem(final TElement element) {
			final Table table= this.viewer.getTable();
			final int idx= indexOf(element);
			if (idx >= 0 && idx < table.getItemCount()) {
				return table.getItem(idx);
			}
			return null;
		}
		
		private void recheckSelection() {
			this.viewer.getTable().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (UIAccess.isOkToUse(CheckboxColumnControl.this.viewer)) {
						final TElement element= getSelectedElement();
						final int idx= indexOf(element);
						if (idx >= 0) {
							CheckboxColumnControl.this.viewer.getTable().setSelection(idx);
						}
					}
				}
			});
		}
		
		public void configureAsMainColumn() {
			this.viewer.getTable().setTabList(new Control[0]);
			final Listener tableListener= new Listener() {
				@Override
				public void handleEvent(final Event event) {
					switch (event.type) {
					case SWT.KeyDown:
						if (event.keyCode == SWT.SPACE) {
							final TElement element= getSelectedElement();
							if (element == null
									|| !CheckboxColumnControl.this.editableElements.contains(element)) {
								return;
							}
							toggle(element);
						}
						return;
						
					case SWT.MouseDown:
						final TableItem item= CheckboxColumnControl.this.viewer.getTable().getItem(new Point(event.x, event.y));
						if (item != null) {
							@SuppressWarnings("unchecked")
							final TElement element= (TElement) item.getData();
							if (element == null 
									|| !CheckboxColumnControl.this.editableElements.contains(element)) {
								return;
							}
							final TableEditor editor= (TableEditor) item.getData("editor");
							final Button button= (Button) editor.getEditor();
							button.setSelection(!button.getSelection());
							doCheck(element, button.getSelection());
						}
						return;
					}
				}
			};
			this.viewer.getTable().addListener(SWT.KeyDown, tableListener);
			this.viewer.getTable().addListener(SWT.MouseDown, tableListener);
		}
		
	}
	
	public static class TreeComposite extends Composite {
		
		public TreeViewer viewer;
		public Tree tree;
		public TreeColumnLayout layout;
		
		public TreeComposite(final Composite parent, final int treeStyle) {
			super(parent, SWT.NONE);
			
			this.layout= new TreeColumnLayout();
			setLayout(this.layout);
			this.tree= new Tree(this, treeStyle);
			this.viewer= new TreeViewer(this.tree);
		}
		
		
		public TreeViewerColumn addColumn(final String title, final int style, final ColumnLayoutData layoutData) {
			final TreeViewerColumn column= new TreeViewerColumn(this.viewer, style);
			column.getColumn().setText(title);
			this.layout.setColumnData(column.getColumn(), layoutData);
			return column;
		}
		
		public TreeViewerColumn addColumn(final int style, final ColumnLayoutData layoutData) {
			final TreeViewerColumn column= new TreeViewerColumn(this.viewer, style);
			this.layout.setColumnData(column.getColumn(), layoutData);
			return column;
		}
		
		public TreeViewerColumn getViewerColumn(final int index) {
			final TreeColumn column= this.tree.getColumn(index);
			if (column == null) {
				return null;
			}
			return (TreeViewerColumn) column.getData(Policy.JFACE + ".columnViewer"); //$NON-NLS-1$
		}
		
	}
	
	public static void installDefaultEditBehaviour(final TableViewer tableViewer) {
		final CellNavigationStrategy naviStrat= new CellNavigationStrategy() {
			@Override
			public ViewerCell findSelectedCell(final ColumnViewer viewer, final ViewerCell currentSelectedCell, final Event event) {
				final ViewerCell cell= super.findSelectedCell(viewer, currentSelectedCell, event);
				if (cell != null ) {
					tableViewer.getTable().showColumn(tableViewer.getTable().getColumn(cell.getColumnIndex()));
				}
				return cell;
			}
			
		};
		final TableViewerFocusCellManager focusCellManager= new TableViewerFocusCellManager(
				tableViewer, new FocusCellOwnerDrawHighlighter(tableViewer), naviStrat);
		TableViewerEditor.create(tableViewer, focusCellManager, createActivationStrategy(tableViewer),
				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_VERTICAL
					| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}
	
	public static void installDefaultEditBehaviour2(final TableViewer tableViewer) {
		final Listener listener= new Listener() {
			@Override
			public void handleEvent(final Event event) {
				switch (event.type) {
				case SWT.KeyDown:
					if (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.F2) {
						final IStructuredSelection selection= (IStructuredSelection) tableViewer.getSelection();
						if (selection.size() >= 1) {
							tableViewer.editElement(selection.getFirstElement(), 0);
						}
					}
					break;
				}
			}
		};
		tableViewer.getControl().addListener(SWT.KeyDown, listener);
	}
	
	public static void installDefaultEditBehaviour(final TreeViewer treeViewer) {
		final CellNavigationStrategy naviStrat= new CellNavigationStrategy() {
			@Override
			public ViewerCell findSelectedCell(final ColumnViewer viewer, final ViewerCell currentSelectedCell, final Event event) {
				final ViewerCell cell= super.findSelectedCell(viewer, currentSelectedCell, event);
				if (cell != null ) {
					treeViewer.getTree().showColumn(treeViewer.getTree().getColumn(cell.getColumnIndex()));
				}
				return cell;
			}
			
		};
		final TreeViewerFocusCellManager focusCellManager= new TreeViewerFocusCellManager(
				treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer), naviStrat);
		TreeViewerEditor.create(treeViewer, focusCellManager, createActivationStrategy(treeViewer),
				ColumnViewerEditor.TABBING_HORIZONTAL | ColumnViewerEditor.TABBING_VERTICAL
				| ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR | ColumnViewerEditor.KEYBOARD_ACTIVATION);
	}
	
	private static ColumnViewerEditorActivationStrategy createActivationStrategy(final ColumnViewer viewer) {
		viewer.getControl().addTraverseListener(new TraverseListener() {
			@Override
			public void keyTraversed(final TraverseEvent e) {
				if (e.detail == SWT.TRAVERSE_RETURN && e.stateMask == SWT.NONE) {
					e.doit= false;
				}
			}
		});
		return new ColumnViewerEditorActivationStrategy(viewer) {
			@Override
			protected boolean isEditorActivationEvent(
					final ColumnViewerEditorActivationEvent event) {
				return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
				|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION
				|| event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
				|| (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED
						&& (event.keyCode == SWT.CR || event.keyCode == SWT.KEYPAD_CR || event.keyCode == SWT.F2) )
						|| event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
			}
		};
		
	}
	
	
	public static void installSearchTextNavigation(final TableViewer viewer,
			final SearchText searchText, final boolean back) {
		final Table table= viewer.getTable();
		searchText.addListener(new SearchText.Listener() {
			@Override
			public void textChanged(final boolean user) {
			}
			@Override
			public void okPressed() {
			}
			@Override
			public void downPressed() {
				table.setFocus();
				if (table.getItemCount() > 0) {
					if (table.getSelectionIndex() < 0) {
						table.select(0);
					}
					else {
						table.showSelection();
					}
				}
			}
		});
		if (back) {
			table.addKeyListener(new org.eclipse.swt.events.KeyAdapter() {
				@Override
				public void keyPressed(final KeyEvent e) {
					if (e.stateMask == 0 && e.keyCode == SWT.ARROW_UP
							&& table.getSelectionCount() == 1
							&& table.getSelectionIndex() == 0) {
						table.deselectAll();
						searchText.setFocus();
					}
				}
			});
		}
	}
	
	
	public static void setSelectionProvider(final Control control, final ISelectionProvider selectionProvider) {
		control.setData(Policy.JFACE + ".selectionProvider", selectionProvider); //$NON-NLS-1$
	}
	
	public static ISelectionProvider getSelectionProvider(final Control control) {
		if (control != null) {
			final Object data= control.getData(Policy.JFACE + ".selectionProvider"); //$NON-NLS-1$
			if (data instanceof ISelectionProvider) {
				return (ISelectionProvider) data;
			}
		}
		return null;
	}
	
	
	public static void scheduleStandardSelection(final TableViewer viewer) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final ISelection selection= viewer.getSelection();
				if (selection.isEmpty()) {
					if (viewer.getTable().getItemCount() > 0) {
						final TableItem item= viewer.getTable().getItem(0);
						viewer.setSelection(new StructuredSelection(item.getData()));
					}
					else {
						viewer.setSelection(new StructuredSelection());
					}
				}
			}
		});
	}
	
	public static void scheduleStandardSelection(final TreeViewer viewer) {
		viewer.getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				final ISelection selection= viewer.getSelection();
				if (selection.isEmpty()) {
					if (viewer.getTree().getItemCount() > 0) {
						final TreeItem item= viewer.getTree().getItem(0);
						viewer.setSelection(new TreeSelection(new TreePath(
								new Object[] { item.getData() } )));
						viewer.setExpandedState(item.getData(), true);
					}
					else {
						viewer.setSelection(new StructuredSelection());
					}
				}
			}
		});
	}
	
	public static ImList<Object> toList(final TreePath path) {
		final int n= path.getSegmentCount();
		switch (n) {
		case 0:
			return ImCollections.emptyList();
		case 1:
			return ImCollections.newList(path.getSegment(0));
		default:
			final Object[] array= new Object[n];
			for (int i= 0; i < n; i++) {
				array[i]= path.getSegment(i);
			}
			return ImCollections.newList(array);
		}
	}
	
	/**
	 * {@link AbstractTreeViewer#expandToLevel(Object, int)}
	 * 
	 * Workaround for E bug #54116
	 */
	public static void expandToLevel(final AbstractTreeViewer viewer, final Object element, final int level) {
		viewer.expandToLevel(element, level);
		for (TreeItem item= (TreeItem) viewer.testFindItem(element); item != null;
				item= item.getParentItem()) {
			item.setExpanded(true);
		}
	}
	
	
	private ViewerUtil() {}
	
}
