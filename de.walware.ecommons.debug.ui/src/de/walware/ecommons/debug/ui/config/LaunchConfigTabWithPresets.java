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

package de.walware.ecommons.debug.ui.config;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.ui.statushandlers.StatusManager;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.components.DropDownButton;

import de.walware.ecommons.debug.internal.ui.Messages;
import de.walware.ecommons.debug.ui.ECommonsDebugUI;


public abstract class LaunchConfigTabWithPresets extends LaunchConfigTabWithDbc {
	
	
	private ImList<ILaunchConfiguration> presets;
	
	private Map<String, Object> presetToLoad;
	private ILaunchConfiguration presetLoaded;
	
	
	protected LaunchConfigTabWithPresets() {
	}
	
	
	protected void setPresets(final LaunchConfigPresets presets) {
		this.presets= presets.toList();
	}
	
	
	protected DropDownButton addPresetsButton(final Composite parent) {
		final DropDownButton button= new DropDownButton(parent, SWT.SINGLE);
		button.setText(Messages.ConfigTab_LoadPreset_label);
		final GridData gd= new GridData(SWT.RIGHT, SWT.FILL, true, false);
		button.setLayoutData(gd);
		
		final Menu menu= button.getDropDownMenu();
		final SelectionListener selectionListener= new SelectionAdapter() {
			@Override
			public void widgetSelected(final SelectionEvent e) {
				loadPreset((ILaunchConfiguration) e.widget.getData());
			}
		};
		for (final ILaunchConfiguration preset : this.presets) {
			final MenuItem item= new MenuItem(menu, SWT.PUSH);
			item.setText(LaunchConfigPresets.getName(preset));
			item.setData(preset);
			item.addSelectionListener(selectionListener);
		}
		
		if (this.presets == null || this.presets.isEmpty()) {
			button.setEnabled(false);
		}
		
		return button;
	}
	
	
	protected ImList<ILaunchConfigurationTab> getPresetTabs(final ILaunchConfiguration config) {
		final ILaunchConfigurationTab[] tabs= getLaunchConfigurationDialog().getTabs();
		if (tabs[tabs.length - 1] instanceof CommonTab) {
			return ImCollections.newList(tabs, 0, tabs.length - 1);
		}
		else {
			return ImCollections.newList();
		}
	}
	
	protected void loadPreset(final ILaunchConfiguration preset) {
		try {
			this.presetToLoad= preset.getAttributes();
			setDirty(true);
			updateLaunchConfigurationDialog();
			
			if (this.presetLoaded != null) {
				final ILaunchConfiguration config= this.presetLoaded;
				final List<ILaunchConfigurationTab> tabs= getPresetTabs(config);
				
				for (final ILaunchConfigurationTab tab : tabs) {
					tab.initializeFrom(config);
				}
			}
		}
		catch (final Exception e) {
			StatusManager.getManager().handle(new Status(IStatus.ERROR, ECommonsDebugUI.PLUGIN_ID, 0,
						"An error occurred while loading the launch configuration preset.", e ),
					(StatusManager.LOG | StatusManager.SHOW) );
		}
		finally {
			this.presetToLoad= null;
			this.presetLoaded= null;
		}
	}
	
	
	@Override
	protected void doInitialize(final ILaunchConfiguration configuration) {
	}
	
	@Override
	public void performApply(final ILaunchConfigurationWorkingCopy configuration) {
		if (this.presetToLoad != null) {
			try {
				configuration.removeAttribute(getValidationErrorAttr());
				
				for (final Entry<String, Object> entry : this.presetToLoad.entrySet()) {
					final String name= entry.getKey();
					if (LaunchConfigPresets.isInternalArgument(name)) {
						continue;
					}
					final Object value= entry.getValue();
					if (value instanceof String) {
						if (value.equals(LaunchConfigPresets.UNDEFINED_VALUE)) {
							configuration.removeAttribute(name);
						}
						else {
							configuration.setAttribute(name, (String) value);
						}
					}
					else if (value instanceof Integer) {
						configuration.setAttribute(name, (Integer) value);
					}
					else if (value instanceof Boolean) {
						configuration.setAttribute(name, (Boolean) value);
					}
					else if (value instanceof List) {
						configuration.setAttribute(name, (List) value);
					}
					else if (value instanceof Map) {
						configuration.setAttribute(name, (Map) value);
					}
				}
				this.presetLoaded= configuration;
				return;
			}
			finally {
				this.presetToLoad= null;
			}
		}
		
		super.performApply(configuration);
	}
	
}
