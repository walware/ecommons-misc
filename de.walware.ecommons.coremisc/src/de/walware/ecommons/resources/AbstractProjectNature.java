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

package de.walware.ecommons.resources;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;

import de.walware.ecommons.preferences.IPreferenceAccess;
import de.walware.ecommons.preferences.Preference;
import de.walware.ecommons.preferences.PreferencesUtil;


public class AbstractProjectNature implements IProjectNature, IPreferenceAccess {
	
	
	private IProject project;
	
	private IScopeContext[] contexts;
	
	
	public AbstractProjectNature() {
		super();
	}
	
	
	@Override
	public void setProject(final IProject project) {
		this.project = project;
		this.contexts = createPrefContexts(true);
	}
	
	@Override
	public final IProject getProject() {
		return this.project;
	}
	
	@Override
	public void configure() throws CoreException {
		addBuilders();
	}
	
	@Override
	public void deconfigure() throws CoreException {
		removeBuilders();
	}
	
	protected void addBuilders() throws CoreException {
	}
	
	protected void removeBuilders() throws CoreException {
	}
	
/*-- IPreferenceAccess -------------------------------------------------------*/
	
	private IScopeContext[] createPrefContexts(final boolean inheritInstanceSettings) {
		return (inheritInstanceSettings) ?
				new IScopeContext[] {
					new ProjectScope(getProject()),
					InstanceScope.INSTANCE,
					DefaultScope.INSTANCE,
				} :
				new IScopeContext[] {
					new ProjectScope(getProject()),
					DefaultScope.INSTANCE,
				};
	}
	
	@Override
	public <T> T getPreferenceValue(final Preference<T> key) {
		return PreferencesUtil.getPrefValue(this.contexts, key);
	}
	
	@Override
	public IEclipsePreferences[] getPreferenceNodes(final String nodeQualifier) {
		return PreferencesUtil.getRelevantNodes(nodeQualifier, this.contexts);
	}
	
	@Override
	public IScopeContext[] getPreferenceContexts() {
		return this.contexts;
	}
	
	@Override
	public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		int i = this.contexts.length-2;
		while (i >= 0) {
			final IEclipsePreferences node = this.contexts[i--].getNode(nodeQualifier);
			if (node != null) {
				node.addPreferenceChangeListener(listener);
			}
		}
	}
	
	@Override
	public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		int i = this.contexts.length-2;
		while (i >= 0) {
			final IEclipsePreferences node = this.contexts[i--].getNode(nodeQualifier);
			if (node != null) {
				node.removePreferenceChangeListener(listener);
			}
		}
	}
	
	public IScopeContext getProjectContext() {
		return this.contexts[0];
	}
	
	
	@Override
	public int hashCode() {
		return getProject().hashCode() + 1;
	}
	
	@Override
	public boolean equals(final Object obj) {
		return (this == obj || (obj != null
				&& getClass().equals(obj.getClass())
				&& getProject().equals(((AbstractProjectNature) obj).getProject()) ));
	}
	
}
