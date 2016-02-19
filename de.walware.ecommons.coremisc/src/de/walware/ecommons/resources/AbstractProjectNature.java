/*=============================================================================#
 # Copyright (c) 2005-2016 Stephan Wahlbrink (WalWare.de) and others.
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

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.core.util.PreferenceAccessWrapper;
import de.walware.ecommons.preferences.core.util.PreferenceUtils;


public class AbstractProjectNature extends PreferenceAccessWrapper implements IProjectNature {
	
	// TODO add dispose (ResourceTracker)
	
	
	private IProject project;
	
	private boolean configured;
	
	
	public AbstractProjectNature() {
		super();
	}
	
	
	@Override
	public void setProject(final IProject project) {
		this.configured= true;
		
		this.project= project;
		super.setPreferenceContexts(createPrefContexts());
	}
	
	@Override
	public void setPreferenceContexts(final ImList<IScopeContext> contexts) {
		throw new UnsupportedOperationException();
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
		this.configured= false;
		
		removeBuilders();
	}
	
	protected void addBuilders() throws CoreException {
	}
	
	protected void removeBuilders() throws CoreException {
	}
	
/*-- IPreferenceAccess -------------------------------------------------------*/
	
	private ImList<IScopeContext> createPrefContexts() {
		return ImCollections.newList(
				new ProjectScope(getProject()),
				InstanceScope.INSTANCE,
				DefaultScope.INSTANCE );
	}
	
	
	@Override
	public void addPreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		final ImList<IScopeContext> contexts= getPreferenceContexts();
		for (int i= 0; i < contexts.size() - 1; i++) {
			final IEclipsePreferences node= contexts.get(i).getNode(nodeQualifier);
			if (node != null) {
				node.addPreferenceChangeListener(listener);
			}
		}
	}
	
	@Override
	public void removePreferenceNodeListener(final String nodeQualifier, final IPreferenceChangeListener listener) {
		final ImList<IScopeContext> contexts= getPreferenceContexts();
		for (int i= 0; i < contexts.size() - 1; i++) {
			final IEclipsePreferences node= contexts.get(i).getNode(nodeQualifier);
			if (node != null) {
				node.addPreferenceChangeListener(listener);
			}
		}
	}
	
	
	protected final IScopeContext getProjectContext() {
		return getPreferenceContexts().get(0);
	}
	
	protected final <T> T getProjectValue(final Preference<T> key) {
		return PreferenceUtils.getPrefValue(getPreferenceContexts().get(0), key);
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
