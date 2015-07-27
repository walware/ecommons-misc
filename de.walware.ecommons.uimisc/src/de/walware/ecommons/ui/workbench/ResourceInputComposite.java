/*=============================================================================#
 # Copyright (c) 2007-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.workbench;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.ui.StringVariableSelectionDialog.VariableFilter;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.io.ObservableFileValidator;
import de.walware.ecommons.ui.SharedMessages;
import de.walware.ecommons.ui.components.CustomizableVariableSelectionDialog;
import de.walware.ecommons.ui.components.WidgetToolsButton;
import de.walware.ecommons.ui.internal.Messages;
import de.walware.ecommons.ui.util.LayoutUtil;
import de.walware.ecommons.ui.util.MessageUtil;


/**
 * Composite with text/combo and browse buttons.
 * 
 * Configurable for files and directories, new or existing resources.
 * 
 * XXX: Not yet all combinations are tested!
 */
public class ResourceInputComposite extends Composite implements IValueChangeListener {
	
	
	private static final String VAR_WORKSPACE_LOC = "workspace_loc"; //$NON-NLS-1$
	
	
	private static class SearchResourceDialog extends FilteredResourcesSelectionDialog {
		
		public SearchResourceDialog(final Shell shell, final boolean multi,
				final IContainer container, final int typesMask) {
			super(shell, multi, container, typesMask);
			setTitle(Messages.ResourceSelectionDialog_title);
		}
	}
	
	
	public static final int MODE_FILE =                     0x00000001;
	public static final int MODE_DIRECTORY =                0x00000002;
	public static final int MODE_SAVE =                     0x00000004;
	public static final int MODE_OPEN =                     0x00000008;
	public static final int MODE_WS_ONLY =                  0x00000010;
	
	public static final int STYLE_TEXT = 0;
	public static final int STYLE_COMBO = 1;
	public static final int STYLE_GROUP = 1<<1;
	public static final int STYLE_LABEL = 2<<1;
	
	
	private String fResourceLabel;
	
	private final int fStyle;
	private final boolean fAsCombo;
	
	private boolean fForDirectory;
	private boolean fForFile;
	private boolean fDoOpen;
	private boolean fWSOnly;
	private boolean fControlledChange;
	
	private final ObservableFileValidator fValidator;
	
	private Text fLocationTextField;
	private Combo fLocationComboField;
	
	private Label fLabel;
	private WidgetToolsButton fTools;
	private boolean fShowInsertVariable;
	private ImList<? extends VariableFilter> fShowInsertVariableFilters;
	private ImList<? extends IStringVariable> fShowInsertVariableAdditionals;
	
	private String fDefaultFilesystemPath;
	private String[] fFileFilters;
	private String[] fFileFilterNames;
	
	
	public ResourceInputComposite(final Composite parent, final int style,
			final int mode, final String resourceLabel) {
		super(parent, SWT.NONE);
		
		fValidator = new ObservableFileValidator(Realm.getDefault());
		setMode(mode);
		
		fStyle = style;
		fAsCombo = (fStyle & STYLE_COMBO) == STYLE_COMBO;
		fControlledChange = false;
		setResourceLabel(resourceLabel);
		createContent();
	}
	
	
	public void setHistory(final String[] history) {
		if (history != null && fAsCombo) {
			fLocationComboField.setItems(history);
		}
	}
	
	public void setMode(final int mode) {
		assert ((mode & (MODE_DIRECTORY | MODE_FILE)) != 0);
		if ((mode & MODE_DIRECTORY) == MODE_DIRECTORY) {
			fForDirectory = true;
			fValidator.setOnDirectory(IStatus.OK);
		}
		else {
			fForDirectory = false;
			fValidator.setOnDirectory(IStatus.ERROR);
		}
		if ((mode & MODE_FILE) == MODE_FILE) {
			fForFile = true;
			fValidator.setOnFile(IStatus.OK);
		}
		else {
			fForFile = false;
			fValidator.setOnFile(IStatus.ERROR);
		}
		
		fDoOpen = (mode & MODE_OPEN) == MODE_OPEN;
		fValidator.setDefaultMode(fDoOpen);
		
		fWSOnly = (mode & MODE_WS_ONLY) == MODE_WS_ONLY;
		fValidator.setRequireWorkspace(fWSOnly, fWSOnly);
		
		if (fWSOnly) {
			fValidator.getWorkspaceResourceObservable().addValueChangeListener(this);
		}
		else {
			fValidator.getFileStoreObservable().addValueChangeListener(this);
		}
		
		if (fTools != null) {
			fTools.resetMenu();
		}
	}
	
	public void setResourceLabel(final String label) {
		fResourceLabel = label;
		if (fLabel != null) {
			fLabel.setText(fResourceLabel + ':');
		}
		fValidator.setResourceLabel(MessageUtil.removeMnemonics(label));
	}
	
	protected String getTaskLabel() {
		return NLS.bind(Messages.ChooseResource_Task_description, fResourceLabel);
	}
	
	public void setShowInsertVariable(final boolean enable,
			final List<VariableFilter> filters, final List<? extends IStringVariable> additionals) {
		fShowInsertVariable = enable;
		fShowInsertVariableFilters= (filters != null) ? ImCollections.toList(filters) : null;
		if (fShowInsertVariableAdditionals != null) {
			for (final IStringVariable variable : fShowInsertVariableAdditionals) {
				final String name = variable.getName();
				final Pattern pattern = Pattern.compile("\\Q${"+name+"\\E[\\}\\:]");  //$NON-NLS-1$//$NON-NLS-2$
				fValidator.setOnPattern(pattern, -1);
			}
		}
		fShowInsertVariableAdditionals= (additionals != null) ? ImCollections.toList(additionals) : null;
		if (fShowInsertVariableAdditionals != null) {
			for (final IStringVariable variable : fShowInsertVariableAdditionals) {
				final String name = variable.getName();
				final Pattern pattern = Pattern.compile("\\Q${"+name+"\\E[\\}\\:]"); //$NON-NLS-1$ //$NON-NLS-2$
				fValidator.setOnPattern(pattern, IStatus.OK);
			}
		}
		if (fTools != null) {
			fTools.resetMenu();
		}
	}
	
	/**
	 * Sets the default filesystem path for file and directory selection dialogs, used if
	 * no resource is specified.
	 * 
	 * @param path the filesystem path
	 */
	public void setDefaultFilesystemPath(final String path) {
		fDefaultFilesystemPath = (path != null && !path.isEmpty()) ? path : null;
	}
	
	/**
	 * Sets the file filters for file selection dialogs.
	 * 
	 * A filter is a string array with:<br/>
	 *     [0] = filter, e.g. "*.txt"<br/>
	 *     [1] = name, e.g. "Text Files"
	 * 
	 * @param filters list of filters 
	 */
	public void setFileFilters(final List<String[]> filters) {
		String[] extensions0;
		String[] names;
		if (filters != null) {
			final int n = filters.size() + 1;
			extensions0 = new String[n];
			names = new String[n];
			for (int i = 0; i < n - 1; i++) {
				final String[] strings = filters.get(i);
				extensions0[i] = strings[0];
				names[i] = strings[1] + " ("+strings[0]+")"; //$NON-NLS-1$ //$NON-NLS-2$
			}
			extensions0[n - 1] = "*.*"; //$NON-NLS-1$
			names[n - 1] = Messages.ChooseResource_AllFiles_name + " (*.*)"; //$NON-NLS-1$
		}
		else {
			extensions0 = null;
			names = null;
		}
		fFileFilters = extensions0;
		fFileFilterNames = names;
	}
	
	
	public Control getTextControl() {
		if (fAsCombo) {
			return fLocationComboField;
		}
		else {
			return fLocationTextField;
		}
	}
	
	protected String escapeText(final String text) {
		if (this.fValidator.getVariableResolver() != null) {
			return this.fValidator.getVariableResolver().escapeText(text);
		}
		return text;
	}
	
	protected void setText(final String text) {
		setText(text, true);
	}
	
	private void setText(final String s, final boolean validate) {
		if (!validate) {
			fControlledChange = true;
		}
		if (fAsCombo) {
			fLocationComboField.setText(s);
		}
		else {
			fLocationTextField.setText(s);
		}
		if (!validate) {
			fControlledChange = false;
		}
	}
	
	protected void insertText(final String s) {
		if (fAsCombo) {
			//
		}
		else {
			fLocationTextField.insert(s);
		}
	}
	
	protected String getText() {
		if (fAsCombo) {
			return fLocationComboField.getText();
		}
		else {
			return fLocationTextField.getText();
		}
	}
	
	private void createContent() {
		Composite content;
		final GridLayout layout;
		if ((fStyle & STYLE_GROUP) == STYLE_GROUP) {
			setLayout(new FillLayout());
			final Group group = new Group(this, SWT.NONE);
			group.setText(fResourceLabel + ':');
			content = group;
			layout = LayoutUtil.createGroupGrid(2);
		}
		else {
			content = this;
			layout = LayoutUtil.createCompositeGrid(2);
		}
		layout.horizontalSpacing = 0;
		content.setLayout(layout);
		
		if ((fStyle & STYLE_LABEL) != 0) {
			fLabel = new Label(content, SWT.LEFT);
			fLabel.setText(fResourceLabel + ':');
			fLabel.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		}
		
		if (fAsCombo) {
			fLocationComboField = new Combo(content, SWT.DROP_DOWN);
		}
		else {
			fLocationTextField = new Text(content, SWT.BORDER | SWT.SINGLE);
		}
		final Control inputField = getTextControl();
		inputField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(final ModifyEvent e) {
				if (!fControlledChange) {
					fValidator.setExplicit(getText());
//					fValidator.getStatus();
				}
			}
		});
		
		fTools = new WidgetToolsButton(inputField) {
			@Override
			protected void fillMenu(final Menu menu) {
				ResourceInputComposite.this.fillMenu(menu);
			}
		};
		fTools.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
	}
	
	protected void fillMenu(final Menu menu) {
		final boolean both = (fForFile && fForDirectory);
		{
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(Messages.SearchWorkspace_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleSearchWorkspaceButton();
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}
		if (fForFile) {
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(both ? Messages.BrowseWorkspace_ForFile_label : Messages.BrowseWorkspace_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleBrowseWorkspaceButton(MODE_FILE);
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}
		if (fForDirectory) {
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(both ? Messages.BrowseWorkspace_ForDir_label : Messages.BrowseWorkspace_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleBrowseWorkspaceButton(MODE_DIRECTORY);
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}	
		if (fForFile && !fWSOnly) {
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(both ? Messages.BrowseFilesystem_ForFile_label : Messages.BrowseFilesystem_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleBrowseFilesystemButton(MODE_FILE);
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}
		if (fForDirectory && !fWSOnly) {
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(both ? Messages.BrowseFilesystem_ForDir_label : Messages.BrowseFilesystem_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleBrowseFilesystemButton(MODE_DIRECTORY);
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}
		
		if (fShowInsertVariable) {
			new MenuItem(menu, SWT.SEPARATOR);
		}
		
		if (fShowInsertVariable) {
			final MenuItem item = new MenuItem(menu, SWT.PUSH);
			item.setText(SharedMessages.InsertVariable_label);
			item.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(final SelectionEvent e) {
					beforeMenuAction();
					handleVariablesButton();
					getTextControl().setFocus();
					afterMenuAction();
				}
			});
		}
		
	}
	
	protected void handleSearchWorkspaceButton() {
		int resourceMode = 0;
		if (fForFile) {
			resourceMode |= IResource.FILE;
		}
		if (fForDirectory ) {
			resourceMode |= IResource.FOLDER;
		}
		final IWorkspaceRoot container = ResourcesPlugin.getWorkspace().getRoot();
		final SearchResourceDialog dialog = new SearchResourceDialog(getShell(), false, container, resourceMode);
		String initial = ""; //$NON-NLS-1$
		final IFileStore store = fValidator.getFileStore();
		if (store != null) {
			initial = store.getName();
		}
		else {
			final String current = getText();
			final int idx = current.lastIndexOf('/');
			if (idx >= 0) {
				initial = current.substring(idx+1);
			}
			else {
				initial = current;
			}
		}
		dialog.setInitialPattern(initial);
		dialog.open();
		final Object[] results = dialog.getResult();
		if (results == null || results.length < 1) {
			return;
		}
		IResource resource = (IResource) results[0];
		if (!fForFile && resource.getType() == IResource.FILE) {
			resource = resource.getParent();
		}
		final String wsPath = resource.getFullPath().toString();
		
		fValidator.setExplicit(resource);
		setText(newVariableExpression(VAR_WORKSPACE_LOC, escapeText(wsPath)), false); 
	}
	
	protected void handleBrowseWorkspaceButton(final int mode) {
		IResource res = fValidator.getWorkspaceResource();
		if (res == null) {
			res = ResourcesPlugin.getWorkspace().getRoot();
		}
		
		Object[] results = null;
		String wsPath;
		String appendPath;
		IResource resource = null;
		if (mode == MODE_DIRECTORY) {
			if (res instanceof IFile) {
				res = res.getParent();
			}
			final ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(), 
					(IContainer) res, (fValidator.getOnNotExisting() != IStatus.ERROR), getTaskLabel());
			dialog.open();
			results = dialog.getResult();
			if (results == null || results.length < 1) {
				return;
			}
			wsPath = ((Path) results[0]).toString();
			resource = ResourcesPlugin.getWorkspace().getRoot().findMember(wsPath);
			appendPath = ""; //$NON-NLS-1$
		}
		else {
			final ResourceSelectionDialog dialog = new ResourceSelectionDialog(getShell(), getTaskLabel());
			dialog.setInitialSelections(new IResource[] { res });
			dialog.setAllowNewResources(fValidator.getOnNotExisting() != IStatus.ERROR);
			dialog.open();
			results = dialog.getResult();
			if (results == null || results.length < 1) {
				return;
			}
			resource= (IFile) results[0];
			res= resource.getParent();
			final StringBuilder path= new StringBuilder(resource.getName());
			path.insert(0, '/');
			while (!res.exists()) {
				res= res.getParent();
				path.insert(0, res.getName());
				path.insert(0, '/');
			}
			wsPath= res.getFullPath().toString();
			appendPath= path.toString();
		}
		
		fValidator.setExplicit(resource);
		if (fWSOnly) {
			setText(escapeText(wsPath + appendPath), false);
		}
		else {
			setText(newVariableExpression(VAR_WORKSPACE_LOC, escapeText(wsPath)) + escapeText(appendPath), false);
		}
	}
	
	protected void handleBrowseFilesystemButton(final int mode) {
		String path = null;
		try {
			if (fValidator.isLocalFile()) {
				path = URIUtil.toPath(fValidator.getFileStore().toURI()).toOSString();
			}
			else if (getText().isEmpty()) {
				path = fDefaultFilesystemPath;
			}
		}
		catch (final Exception e) {
		}
		if (mode == MODE_DIRECTORY) {
			final DirectoryDialog dialog = new DirectoryDialog(getShell());
			dialog.setText(MessageUtil.removeMnemonics(getTaskLabel()));
			dialog.setFilterPath(path);
			path = dialog.open();
		}
		else {
			final FileDialog dialog = new FileDialog(getShell(), (fDoOpen) ? SWT.OPEN: SWT.SAVE);
			dialog.setText(MessageUtil.removeMnemonics(getTaskLabel()));
			dialog.setFilterPath(path);
			if (fFileFilters != null) {
				dialog.setFilterExtensions(fFileFilters);
				dialog.setFilterNames(fFileFilterNames);
			}
			path = dialog.open();
		}
		if (path == null) {
			return;
		}
		
		path= escapeText(path);
		fValidator.setExplicit(path);
		setText(path, false);
	}
	
	protected void handleVariablesButton() {
		final CustomizableVariableSelectionDialog dialog = new CustomizableVariableSelectionDialog(getShell());
		if (fShowInsertVariableFilters != null) {
			for (final VariableFilter filter : fShowInsertVariableFilters) {
				dialog.addVariableFilter(filter);
			}
		}
		if (fShowInsertVariableAdditionals != null) {
			dialog.setAdditionals(fShowInsertVariableAdditionals);
		}
		else if (this.fValidator.getVariableResolver() != null) {
			final Map<String, IStringVariable> extraVariables= this.fValidator.getVariableResolver()
					.getExtraVariables();
			if (extraVariables != null) {
				dialog.setAdditionals(extraVariables.values());
			}
		}
		if (dialog.open() != Dialog.OK) {
			return;
		}
		final String variable = dialog.getVariableExpression();
		if (variable == null) {
			return;
		}
		insertText(variable);
	}
	
	@Override
	public void handleValueChange(final ValueChangeEvent event) {
		String path= null;
		if (fWSOnly) {
			final IResource resource= fValidator.getWorkspaceResource();
			if (resource != null) {
				path= resource.getFullPath().toString();
			}
		}
		else {
			final IFileStore fileStore= fValidator.getFileStore();
			if (fileStore != null) {
				try {
					if (fValidator.isLocalFile()) {
						path= URIUtil.toPath(fileStore.toURI()).toOSString();
					}
					else {
						path= fileStore.toURI().toString();
					}
				}
				catch (final Exception e) {
				}
			}
		}
		if (path != null && !path.isEmpty()) {
//			path= "\u21FF " + path; //$NON-NLS-1$
		}
		else {
			path= ""; //$NON-NLS-1$
		}
		getTextControl().setToolTipText(path);
	}
	
	
	public String getResourceString() {
		return getText();
	}
	
	
	public void addModifyListener(final ModifyListener listener) {
		if (fAsCombo) {
			fLocationComboField.addModifyListener(listener);
		}
		else {
			fLocationTextField.addModifyListener(listener);
		}
	}
	
	public IResource getResourceAsWorkspaceResource() {
		return fValidator.getWorkspaceResource();
	}
	
	/**
	 * You must call validate, before this method can return a file
	 * @return a file handler or null.
	 */
	public IFileStore getResourceAsFileStore() {
		return fValidator.getFileStore();
	}
	
	public IObservableValue getObservable() {
		if (fAsCombo) {
			return WidgetProperties.text().observe(fLocationComboField);
		}
		else {
			return WidgetProperties.text(SWT.Modify).observe(fLocationTextField);
		}
	}
	
	public ObservableFileValidator getValidator() {
		return fValidator;
	}
	
	/**
	 * Returns a new variable expression with the given variable and the given
	 * argument.
	 * 
	 * @see IStringVariableManager#generateVariableExpression(String, String)
	 */
	protected String newVariableExpression(final String varName, final String arg) {
		return VariablesPlugin.getDefault().getStringVariableManager().generateVariableExpression(varName, arg);
	}
	
	/**
	 * Is called before a menu action is executed.
	 */
	protected void beforeMenuAction() {
	}
	
	/**
	 * Is called after a menu action is finish.
	 */
	protected void afterMenuAction() {
	}
	
}
