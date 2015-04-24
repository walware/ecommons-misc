/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.util;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.internal.core.LaunchConfiguration;


/**
 * Launch configuration which allows to adds temporary additional or overwrite existing
 * attributes to a launch configuration.
 */
@SuppressWarnings({ "restriction" })
public class OverlayLaunchConfiguration extends LaunchConfiguration {
	
	
	private final ILaunchConfiguration launchConfiguration;
	
	private final Map<String, Object> additionalAttributes;
	
	
	public OverlayLaunchConfiguration(final ILaunchConfiguration orginal, final Map<String, Object> additional) {
		super(orginal.getName(), null);
		this.launchConfiguration = orginal;
		this.additionalAttributes = additional;
	}
	
	
	public ILaunchConfiguration getOriginal() {
		return this.launchConfiguration;
	}
	
	
	@Override
	public boolean contentsEqual(final ILaunchConfiguration configuration) {
		return this.launchConfiguration.contentsEqual(configuration);
	}
	
	@Override
	public ILaunchConfigurationWorkingCopy copy(final String name) throws CoreException {
		return this.launchConfiguration.copy(name);
	}
	
	@Override
	public void delete() throws CoreException {
		this.launchConfiguration.delete();
	}
	
	@Override
	public boolean exists() {
		return this.launchConfiguration.exists();
	}
	
	@Override
	public boolean hasAttribute(final String attributeName) throws CoreException {
		if (this.additionalAttributes.containsKey(attributeName)) {
			return true;
		}
		return this.launchConfiguration.hasAttribute(attributeName);
	}
	
	@Override
	public boolean getAttribute(final String attributeName, final boolean defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof Boolean) {
			return ((Boolean) obj).booleanValue();
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public int getAttribute(final String attributeName, final int defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof Integer) {
			return ((Integer) obj).intValue();
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public List getAttribute(final String attributeName, final List defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof List) {
			return (List) obj;
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public Set getAttribute(final String attributeName, final Set defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof Set) {
			return ((Set) obj);
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public Map getAttribute(final String attributeName, final Map defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof Map) {
			return (Map) obj;
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public String getAttribute(final String attributeName, final String defaultValue) throws CoreException {
		final Object obj = this.additionalAttributes.get(attributeName);
		if (obj instanceof String) {
			return (String) obj;
		}
		return this.launchConfiguration.getAttribute(attributeName, defaultValue);
	}
	
	@Override
	public Map getAttributes() throws CoreException {
		return this.launchConfiguration.getAttributes();
	}
	
	@Override
	public String getCategory() throws CoreException {
		return this.launchConfiguration.getCategory();
	}
	
	@Override
	public IFile getFile() {
		return this.launchConfiguration.getFile();
	}
	
	@Override
	@Deprecated
	public IPath getLocation() {
		return this.launchConfiguration.getLocation();
	}
	
	@Override
	public IFileStore getFileStore() throws CoreException {
		if (this.launchConfiguration instanceof LaunchConfiguration) {
			return ((LaunchConfiguration) this.launchConfiguration).getFileStore();
		}
		return super.getFileStore();
	}
	
	@Override
	public IResource[] getMappedResources() throws CoreException {
		return this.launchConfiguration.getMappedResources();
	}
	
	@Override
	public String getMemento() throws CoreException {
		return this.launchConfiguration.getMemento();
	}
	
	@Override
	public Set getModes() throws CoreException {
		return this.launchConfiguration.getModes();
	}
	
	@Override
	public String getName() {
		return this.launchConfiguration.getName();
	}
	
	@Override
	public ILaunchDelegate getPreferredDelegate(final Set modes) throws CoreException {
		return this.launchConfiguration.getPreferredDelegate(modes);
	}
	
	@Override
	public ILaunchConfigurationType getType() throws CoreException {
		return this.launchConfiguration.getType();
	}
	
	@Override
	public ILaunchConfigurationWorkingCopy getWorkingCopy() throws CoreException {
		return this.launchConfiguration.getWorkingCopy();
	}
	
	@Override
	public boolean isLocal() {
		return this.launchConfiguration.isLocal();
	}
	
	@Override
	public boolean isMigrationCandidate() throws CoreException {
		return this.launchConfiguration.isMigrationCandidate();
	}
	
	@Override
	public boolean isReadOnly() {
		return this.launchConfiguration.isReadOnly();
	}
	
	@Override
	public boolean isWorkingCopy() {
		return false;
	}
	
	
	@Override
	public void migrate() throws CoreException {
		this.launchConfiguration.migrate();
	}
	
	@Override
	public boolean supportsMode(final String mode) throws CoreException {
		return this.launchConfiguration.supportsMode(mode);
	}
	
	@Override
	public Object getAdapter(final Class adapter) {
		return this.launchConfiguration.getAdapter(adapter);
	}
	
}
