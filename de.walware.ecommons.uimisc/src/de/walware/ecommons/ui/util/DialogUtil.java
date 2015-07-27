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

package de.walware.ecommons.ui.util;

import java.util.LinkedHashSet;
import java.util.List;

import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.debug.ui.StringVariableSelectionDialog.VariableFilter;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import de.walware.ecommons.collections.ConstArrayList;
import de.walware.ecommons.collections.ImCollections;


/**
 * Util methods for dialogs
 */
public class DialogUtil {
	
	
	public static final int HISTORY_MAX= 25;
	
	private static final String[] EMPTY_ARRAY_SETTING= new String[0];
	
	
	public static IDialogSettings getDialogSettings(final AbstractUIPlugin plugin, final String dialogId) {
		final String sectionName= dialogId;
		final IDialogSettings settings= plugin.getDialogSettings();
		IDialogSettings section= settings.getSection(sectionName);
		if (section == null) {
			section= settings.addNewSection(sectionName);
		}
		return section;
	}
	
	public static IDialogSettings getSection(final IDialogSettings settings, final String sectionName) {
		IDialogSettings section= settings.getSection(sectionName);
		if (section == null) {
			section= settings.addNewSection(sectionName);
		}
		return section;
	}
	
	/**
	 * Combines existing items and new item of a history setting
	 * and saves it to the dialog settings section
	 * 
	 * @param settings settings section
	 * @param key settings key
	 * @param newItem optional new item
	 */
	public static void saveHistorySettings(final IDialogSettings settings, final String key,
			final String newItem) {
		final String[] items= combineHistoryItems(settings.getArray(key), newItem);
		settings.put(key, items);
	}
	
	/**
	 * Combines existing items and new item of a history setting
	 * 
	 * @param existingItems optional array of existing items
	 * @param newItem optional new item
	 */
	public static String[] combineHistoryItems(final String[] existingItems, final String newItem) {
		final LinkedHashSet<String> history= new LinkedHashSet<String>(HISTORY_MAX);
		if (newItem != null && newItem.length() > 0) {
			history.add(newItem);
		}
		if (existingItems != null) {
			for (int i= 0; i < existingItems.length && history.size() < HISTORY_MAX; i++) {
				history.add(existingItems[i]);
			}
		}
		return history.toArray(new String[history.size()]);
	}
	
	public static String[] noNull(final String[] array) {
		return (array != null) ? array : EMPTY_ARRAY_SETTING;
	}
	
	
	/**
	 * Recursively enables/disables all controls and their children.
	 * {@link Control#setEnabled(boolean)}
	 * 
	 * See {@link org.eclipse.jface.dialogs.ControlEnableState ControlEnableState}
	 * if saving state is required.
	 * 
	 * @param control a control
	 * @param exceptions
	 * @param enable
	 */
	public static void setEnabled(final Control control, final List<? extends Control> exceptions, final boolean enable) {
		setEnabled(new Control[] { control }, exceptions, enable);
	}
	
	/**
	 * Recursively enables/disables all controls and their children.
	 * {@link Control#setEnabled(boolean)}
	 * 
	 * See {@link org.eclipse.jface.dialogs.ControlEnableState ControlEnableState}
	 * if saving state is required.
	 * 
	 * @param control array of controls
	 * @param exceptions
	 * @param enable
	 */
	public static void setEnabled(final Control[] controls, final List<? extends Control> exceptions, final boolean enable) {
		for (final Control control : controls) {
			if ((exceptions != null && exceptions.contains(control))) {
				continue;
			}
			control.setEnabled(enable);
			if (control instanceof Composite) {
				final Composite c= (Composite) control;
				final Control[] children= c.getChildren();
				if (children.length > 0) {
					setEnabled(children, exceptions, enable);
				}
			}
		}
	}
	
	/**
	 * Recursively enables/disables all controls and their children.
	 * {@link Control#setEnabled(boolean)}
	 * 
	 * See {@link org.eclipse.jface.dialogs.ControlEnableState ControlEnableState}
	 * if saving state is required.
	 * 
	 * @param control list of controls
	 * @param exceptions
	 * @param enable
	 */
	public static void setEnabled(final List<? extends Control> controls, final List<? extends Control> exceptions, final boolean enable) {
		for (final Control control : controls) {
			if ((exceptions != null && exceptions.contains(control))) {
				continue;
			}
			control.setEnabled(enable);
			if (control instanceof Composite) {
				final Composite c= (Composite) control;
				final Control[] children= c.getChildren();
				if (children.length > 0) {
					setEnabled(children, exceptions, enable);
				}
			}
		}
	}
	
	/**
	 * Recursively sets visible/invisible to the control and its children.
	 * {@link Control#setVisible(boolean)}
	 * 
	 * @param control a control
	 * @param exceptions
	 * @param enable
	 */
	public static void setVisible(final Control control, final List<? extends Control> exceptions, final boolean enable) {
		setVisible(new ConstArrayList<Control>(control), exceptions, enable);
	}
	
	/**
	 * Recursively sets visible/invisible to all controls and their children.
	 * {@link Control#setVisible(boolean)}
	 * 
	 * @param control array of controls
	 * @param exceptions
	 * @param enable
	 */
	public static void setVisible(final Control[] controls, final List<? extends Control> exceptions, final boolean enable) {
		for (final Control control : controls) {
			if ((exceptions != null && exceptions.contains(control))) {
				continue;
			}
			control.setVisible(enable);
			if (control instanceof Composite) {
				final Composite c= (Composite) control;
				final Control[] children= c.getChildren();
				if (children.length > 0) {
					setVisible(children, exceptions, enable);
				}
			}
		}
	}
	
	/**
	 * Recursively sets visible/invisible to all controls and their children.
	 * {@link Control#setVisible(boolean)}
	 * 
	 * @param control list of controls
	 * @param exceptions
	 * @param enable
	 */
	public static void setVisible(final List<? extends Control> controls, final List<? extends Control> exceptions, final boolean enable) {
		for (final Control control : controls) {
			if ((exceptions != null && exceptions.contains(control))) {
				continue;
			}
			control.setVisible(enable);
			if (control instanceof Composite) {
				final Composite c= (Composite) control;
				final Control[] children= c.getChildren();
				if (children.length > 0) {
					setVisible(children, exceptions, enable);
				}
			}
		}
	}
	
	public static Monitor getClosestMonitor(final Display toSearch, final Rectangle rectangle) {
		int closest= Integer.MAX_VALUE;
		
		final Point toFind= Geometry.centerPoint(rectangle);
		final Monitor[] monitors= toSearch.getMonitors();
		Monitor result= monitors[0];
		
		for (int idx= 0; idx < monitors.length; idx++) {
			final Monitor current= monitors[idx];
			
			final Rectangle clientArea= current.getClientArea();
			
			if (clientArea.contains(toFind)) {
				return current;
			}
			
			final int distance= Geometry.distanceSquared(Geometry.centerPoint(clientArea), toFind);
			if (distance < closest) {
				closest= distance;
				result= current;
			}
		}
		
		return result;
	}
	
	
	/**
	 * Variable filter excluding known variables, which requires interaction from the user,
	 * including selection in the UI.
	 */
	public static final VariableFilter EXCLUDE_INTERACTIVE_FILTER= new VariableFilter() {
		@Override
		public boolean isFiltered(final IDynamicVariable variable) {
			final String variableName= variable.getName();
			return (variableName.startsWith("selected_") //$NON-NLS-1$
					|| variableName.endsWith("_prompt") ); //$NON-NLS-1$
		}
	};
	
	/**
	 * Variable filter excluding known variables from Eclipse Development Tool for Java like JDT.
	 */
	public static final VariableFilter EXCLUDE_JAVA_FILTER= new VariableFilter() {
		@Override
		public boolean isFiltered(final IDynamicVariable variable) {
			final String variableName= variable.getName();
			return (variableName.startsWith("java_") //$NON-NLS-1$
					|| variableName.startsWith("target_home") //$NON-NLS-1$
					|| variableName.startsWith("tptp_junit") ); //$NON-NLS-1$
		}
	};
	
	/**
	 * Variable filter excluding known variables, which are only valid in builds.
	 */
	public static final VariableFilter EXCLUDE_BUILD_FILTER= new VariableFilter() {
		@Override
		public boolean isFiltered(final IDynamicVariable variable) {
			final String variableName= variable.getName();
			return (variableName.startsWith("build_")); //$NON-NLS-1$
		}
	};
	
	/**
	 * Variable filter excluding known variables for path location.
	 */
	public static final VariableFilter EXCLUDE_LOC_FILTER= new VariableFilter() {
		@Override
		public boolean isFiltered(final IDynamicVariable variable) {
			final String variableName= variable.getName();
			return (variableName.endsWith("_loc")); //$NON-NLS-1$
		}
	};
	
	
	/**
	 * Common set of filters for use cases, in which user interaction is possible.
	 */
	public static final List<VariableFilter> DEFAULT_INTERACTIVE_FILTERS= ImCollections.newList(
			EXCLUDE_JAVA_FILTER,
			EXCLUDE_BUILD_FILTER );
	
	/**
	 * Common set of filters for use cases, in which user interaction is possible, for workspace resources.
	 */
	public static final List<VariableFilter> DEFAULT_INTERACTIVE_RESOURCE_FILTERS= ImCollections.newList(
			EXCLUDE_JAVA_FILTER,
			EXCLUDE_BUILD_FILTER,
			EXCLUDE_LOC_FILTER );
	
	/**
	 * Common set of filters for use cases, in which user interaction is not possible.
	 */
	public static final List<VariableFilter> DEFAULT_NON_ITERACTIVE_FILTERS= ImCollections.newList(
			EXCLUDE_JAVA_FILTER,
			EXCLUDE_BUILD_FILTER,
			EXCLUDE_INTERACTIVE_FILTER );
	
	
	private DialogUtil() {}
	
}
