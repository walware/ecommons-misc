/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.masterdetail.IObservableFactory;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;
import org.eclipse.ui.preferences.IWorkingCopyManager;
import org.osgi.service.prefs.BackingStoreException;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;

import de.walware.ecommons.IStatusChangeListener;
import de.walware.ecommons.databinding.jface.DataBindingSupport;
import de.walware.ecommons.io.BuildUtil;
import de.walware.ecommons.preferences.SettingsChangeNotifier;
import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.Preference;


/**
 * Allows load, save, restore of managed preferences, including:
 * <p><ul>
 * <li>Connected databinding context:<ul>
 *     <li>use {@link #initBindings()} to create dbc</li>
 *     <li>use {@link #createObservable(Object)} to create observables for model</li>
 *     <li>override {@link #addBindings(DataBindingSupport)}) to register bindings</li>
 *   </ul></li>
 *   <li>optional project scope</li>
 *   <li>change settings groups ({@link SettingsChangeNotifier})</li>
 * </ul>
 * Instead of data binding, it is possible to overwrite
 *   {@link #updatePreferences()} and {@link #updateControls()}
 * to map the preferences to UI.
 */
public abstract class ManagedConfigurationBlock extends ConfigurationBlock
		implements IPreferenceAccess, IObservableFactory {
	
	
	protected class PreferenceManager {
		
		private final ImList<IScopeContext> lookupOrder;
		private final IScopeContext inheritScope;
		private final Map<Preference<?>, String> preferences;
		
		/** Manager for a working copy of the preferences */
		private final IWorkingCopyManager manager;
		/** Map saving the project settings, if disabled */
		private Map<Preference<?>, Object> disabledProjectSettings;
		
		
		PreferenceManager(final Map<Preference<?>, String> prefs) {
			this.manager= getContainer().getWorkingCopyManager();
			this.preferences= prefs;
			
			ManagedConfigurationBlock.this.preferenceManager= this;
			
			if (ManagedConfigurationBlock.this.project != null) {
				this.lookupOrder= ImCollections.newList(
						new ProjectScope(ManagedConfigurationBlock.this.project),
						InstanceScope.INSTANCE,
						DefaultScope.INSTANCE );
				this.inheritScope= null;
			}
			else {
				this.lookupOrder= ImCollections.newList(
						InstanceScope.INSTANCE,
						DefaultScope.INSTANCE );
				this.inheritScope= this.lookupOrder.get(1);
			}
			
			// testIfOptionsComplete();
			
			// init disabled settings, if required
			if (ManagedConfigurationBlock.this.project == null || hasProjectSpecificSettings(ManagedConfigurationBlock.this.project)) {
				this.disabledProjectSettings= null;
			} else {
				saveDisabledProjectSettings();
			}
		}
		
		
/* Managing methods ***********************************************************/
		
		/**
		 * Checks, if project specific options exists
		 * 
		 * @param project to look up
		 * @return
		 */
		boolean hasProjectSpecificSettings(final IProject project) {
			final IScopeContext projectContext= new ProjectScope(project);
			for (final Preference<?> pref : this.preferences.keySet()) {
				if (getInternalValue(pref, projectContext, true) != null) {
					return true;
				}
			}
			return false;
		}
		
		void setUseProjectSpecificSettings(final boolean enable) {
			final boolean hasProjectSpecificOption= (this.disabledProjectSettings == null);
			if (enable != hasProjectSpecificOption) {
				if (enable) {
					loadDisabledProjectSettings();
				} else {
					saveDisabledProjectSettings();
				}
			}
		}
		
		private void saveDisabledProjectSettings() {
			this.disabledProjectSettings= new IdentityHashMap<>();
			for (final Preference<?> pref : this.preferences.keySet()) {
				this.disabledProjectSettings.put(pref, getValue(pref));
				setInternalValue(pref, null); // clear project settings
			}
			
		}
		
		private void loadDisabledProjectSettings() {
			for (final Preference<?> key : this.preferences.keySet()) {
				// Copy values from saved disabled settings to working store
				setValue((Preference) key, this.disabledProjectSettings.get(key));
			}
			this.disabledProjectSettings= null;
		}
		
		boolean processChanges(final boolean saveStore) {
			final List<Preference<?>> changedPrefs= new ArrayList<>();
			final boolean needsBuild= getChanges(changedPrefs);
			if (changedPrefs.isEmpty()) {
				return true;
			}
			
			boolean doBuild= false;
			if (needsBuild) {
				final String[] strings= getFullBuildDialogStrings(getProject() == null);
				if (strings != null) {
					final MessageDialog dialog= new MessageDialog(getShell(),
							strings[0], null, strings[1],
							MessageDialog.QUESTION, new String[] { IDialogConstants.YES_LABEL, IDialogConstants.NO_LABEL, IDialogConstants.CANCEL_LABEL }, 2);
					final int res= dialog.open();
					if (res == 0) {
						doBuild= true;
					}
					else if (res != 1) {
						return false; // cancel pressed
					}
				}
			}
			if (saveStore) {
				try {
					this.manager.applyChanges();
				} catch (final BackingStoreException e) {
					logSaveError(e);
					return false;
				}
				if (doBuild) {
					BuildUtil.getBuildJob(getProject()).schedule();
				}
			}
			else {
				if (doBuild) {
					getContainer().registerUpdateJob(BuildUtil.getBuildJob(getProject()));
				}
			}
			final Set<String> groupIds= new HashSet<>();
			for (final Preference<?> pref : changedPrefs) {
				final String groupId= this.preferences.get(pref);
				if (groupId != null) {
					groupIds.add(groupId);
				}
			}
			scheduleChangeNotification(groupIds, saveStore);
			return true;
		}
		
		/**
		 * 
		 * @param currContext
		 * @param changedSettings
		 * @return true, if rebuild is required.
		 */
		private boolean getChanges(final List<Preference<?>> changedSettings) {
			final IScopeContext currContext= this.lookupOrder.get(0);
			boolean needsBuild= false;
			for (final Preference<?> key : this.preferences.keySet()) {
				final String oldValue= getInternalValue(key, currContext, false);
				final String value= getInternalValue(key, currContext, true);
				if (value == null) {
					if (oldValue != null) {
						changedSettings.add(key);
						needsBuild |= !oldValue.equals(getInternalValue(key, true));
					}
				}
				else if (!value.equals(oldValue)) {
					changedSettings.add(key);
					needsBuild |= (oldValue != null || !value.equals(getInternalValue(key, true)));
					
					if (this.inheritScope != null
							&& value.equals(getInternalValue(key, this.inheritScope, false) )) {
						final IEclipsePreferences node= getNode(currContext, key.getQualifier(), true);
						node.remove(key.getKey());
					}
				}
			}
			return needsBuild;
		}
		
		
		void loadDefaults() {
			final IScopeContext defaultScope= DefaultScope.INSTANCE;
			for (final Preference<?> key : this.preferences.keySet()) {
				final String defValue= getInternalValue(key, defaultScope, false);
				setInternalValue(key, defValue);
			}
		}
		
		// DEBUG
		private void testIfOptionsComplete() {
			for (final Preference<?> key : this.preferences.keySet()) {
				if (getInternalValue(key, false) == null) {
					System.out.println("preference option missing: " + key + " (" + this.getClass().getName() +')');  //$NON-NLS-1$//$NON-NLS-2$
				}
			}
		}
		
		private IEclipsePreferences getNode(final IScopeContext context, final String qualifier, final boolean useWorkingCopy) {
			final IEclipsePreferences node= context.getNode(qualifier);
			if (useWorkingCopy && context != DefaultScope.INSTANCE) {
				return this.manager.getWorkingCopy(node);
			}
			return node;
		}
		
		private String getInternalValue(final Preference<?> pref, final IScopeContext context, final boolean useWorkingCopy) {
			final IEclipsePreferences node= getNode(context, pref.getQualifier(), useWorkingCopy);
			return node.get(pref.getKey(), null);
		}
		
		private String getInternalValue(final Preference<?> pref, final boolean ignoreTopScope) {
			for (int i= ignoreTopScope ? 1 : 0; i < this.lookupOrder.size(); i++) {
				final String value= getInternalValue(pref, this.lookupOrder.get(i), true);
				if (value != null) {
					return value;
				}
			}
			return null;
		}
		
		private <T> void setInternalValue(final Preference<T> pref, final String value) {
			final IEclipsePreferences node= getNode(this.lookupOrder.get(0), pref.getQualifier(), true);
			if (value != null) {
				node.put(pref.getKey(), value);
			}
			else {
				node.remove(pref.getKey());
			}
		}
		
		
		private <T> void setValue(final Preference<T> pref, final T value) {
			final IEclipsePreferences node= getNode(this.lookupOrder.get(0), pref.getQualifier(), true);
			final String storeValue;
			if (value == null
					|| (storeValue= pref.usage2Store(value)) == null) {
				node.remove(pref.getKey());
				return;
			}
			node.put(pref.getKey(), storeValue);
		}
		
		private <T> T getValue(final Preference<T> pref) {
			IEclipsePreferences node= null;
			for (int i= 0; i < this.lookupOrder.size(); i++) {
				final IEclipsePreferences nodeToCheck= getNode(this.lookupOrder.get(i), pref.getQualifier(), true);
				if (nodeToCheck.get(pref.getKey(), null) != null) {
					node= nodeToCheck;
					break;
				}
			}
			
			final String storeValue= (node != null) ? node.get(pref.getKey(), null) : null;
			return pref.store2Usage(storeValue);
		}
	}
	
	
	private final IProject project;
	private PreferenceManager preferenceManager;
	
	private DataBindingSupport dataBinding;
	private IStatusChangeListener statusListener;
	
	private Composite pageComposite;
	
	
	protected ManagedConfigurationBlock(final IProject project) {
		this(project, null, null);
	}
	
	protected ManagedConfigurationBlock(final IProject project, final IStatusChangeListener statusListener) {
		this(project, null, statusListener);
	}
	
	protected ManagedConfigurationBlock(final IProject project, final String title,
			final IStatusChangeListener statusListener) {
		super(title);
		this.project= project;
		this.statusListener= statusListener;
	}
	
	
	protected void setStatusListener(final IStatusChangeListener listener) {
		this.statusListener= listener;
	}
	
	
	public final IProject getProject() {
		return this.project;
	}
	
	@Override
	public void createContents(final Composite pageComposite,
			final IWorkbenchPreferenceContainer container, final IPreferenceStore preferenceStore) {
		this.pageComposite= pageComposite;
		super.createContents(pageComposite, container, preferenceStore);
	}
	
	/**
	 * initialize preference management
	 * 
	 * @param container
	 * @param prefs map with preference objects as key and their settings group id as optional value
	 */
	protected void setupPreferenceManager(final Map<Preference<?>, String> prefs) {
		new PreferenceManager(prefs);
	}
	
	protected void initBindings() {
		this.dataBinding= new DataBindingSupport(this.pageComposite);
		addBindings(this.dataBinding);
		
		this.dataBinding.installStatusListener(this.statusListener);
	}
	
	protected DataBindingSupport getDataBinding() {
		return this.dataBinding;
	}
	
	protected void addBindings(final DataBindingSupport db) {
	}
	
	/**
	 * Point to hook, before the managed preference values are saved to store.
	 * E.g. you can set some additional (or all) values.
	 */
	protected void updatePreferences() {
	}
	
	@Override
	public void performApply() {
		if (this.preferenceManager != null) {
			updatePreferences();
			this.preferenceManager.processChanges(true);
		}
	}
	
	@Override
	public boolean performOk() {
		if (this.preferenceManager != null) {
			updatePreferences();
			return this.preferenceManager.processChanges(false);
		}
		return true;
	}
	
	@Override
	public void performDefaults() {
		if (this.preferenceManager != null) {
			this.preferenceManager.loadDefaults();
			updateControls();
		}
	}
	
	
/* */
	
	/**
	 * Checks, if project specific options exists
	 * 
	 * @param project to look up
	 * @return
	 */
	public boolean hasProjectSpecificOptions(final IProject project) {
		if (project != null && this.preferenceManager != null) {
			return this.preferenceManager.hasProjectSpecificSettings(project);
		}
		return false;
	}
	
	@Override
	public void setUseProjectSpecificSettings(final boolean enable) {
		super.setUseProjectSpecificSettings(enable);
		if (this.project != null && this.preferenceManager != null) {
			this.preferenceManager.setUseProjectSpecificSettings(enable);
		}
	}
	
	protected void updateControls() {
		if (this.dataBinding != null) {
			this.dataBinding.getContext().updateTargets();
		}
	}
	
	
/* Access preference values ***************************************************/
	
	@Override
	public ImList<IScopeContext> getPreferenceContexts() {
		assert (this.preferenceManager != null);
		
		return this.preferenceManager.lookupOrder;
	}
	
	/**
	 * Returns the value for the specified preference.
	 * 
	 * @param pref preference key
	 * @return value of the preference
	 */
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getPreferenceValue(final Preference<T> pref) {
		assert (this.preferenceManager != null);
		assert (pref != null);
		
		if (this.preferenceManager.disabledProjectSettings != null) {
			return (T) this.preferenceManager.disabledProjectSettings.get(pref);
		}
		return this.preferenceManager.getValue(pref);
	}
	
	/**
	 * Sets a preference value in the default store.
	 * 
	 * @param key preference key
	 * @param value new value
	 * @return old value
	 */
	@SuppressWarnings("unchecked")
	public <T> T setPrefValue(final Preference<T> key, final T value) {
		assert (this.preferenceManager != null);
		
		if (this.preferenceManager.disabledProjectSettings != null) {
			return (T) this.preferenceManager.disabledProjectSettings.put(key, value);
		}
		final T oldValue= getPreferenceValue(key);
		this.preferenceManager.setValue(key, value);
		return oldValue;
	}
	
	public void setPrefValues(final Map<Preference<?>, Object> map) {
		for (final Entry<Preference<?>, Object> entry : map.entrySet()) {
			setPrefValue((Preference) entry.getKey(), entry.getValue());
		}
	}
	
	/**
	 * Not (yet) supported
	 */
	@Override
	public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not (yet) supported
	 */
	@Override
	public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not (yet) supported
	 */
	@Override
	
	public void addPreferenceSetListener(final IPreferenceSetService.IChangeListener listener,
			final ImSet<String> qualifiers) {
		throw new UnsupportedOperationException();
	}
	
	/**
	 * Not (yet) supported
	 */
	@Override
	public void removePreferenceSetListener(final IPreferenceSetService.IChangeListener listener) {
		throw new UnsupportedOperationException();
	}
	
	
	@Override
	public IObservableValue createObservable(final Object target) {
		return createObservable((Preference<?>) target);
	}
	
	public IObservableValue createObservable(final Preference<?> pref) {
		return new AbstractObservableValue() {
			@Override
			public Object getValueType() {
				return pref.getUsageType();
			}
			@Override
			protected void doSetValue(final Object value) {
				setPrefValue((Preference) pref, value);
			}
			@Override
			protected Object doGetValue() {
				return getPreferenceValue(pref);
			}
			@Override
			public synchronized void dispose() {
				super.dispose();
			}
		};
	}
	
	/**
	 * Changes requires full build, this method should be overwritten
	 * and return the Strings for the dialog.
	 * 
	 * @param workspaceSettings true, if settings for workspace; false, if settings for project.
	 * @return
	 */
	protected String[] getFullBuildDialogStrings(final boolean workspaceSettings) {
		return null;
	}
	
}
