/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.variables;

import static de.walware.ecommons.debug.core.variables.ResourceVariables.CONTAINER_ENC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.CONTAINER_LOC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.CONTAINER_NAME_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.CONTAINER_PATH_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.FILE_NAME_BASE_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.FILE_NAME_EXT_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.PROJECT_ENC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.PROJECT_LOC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.PROJECT_NAME_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.PROJECT_PATH_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.RESOURCE_ENC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.RESOURCE_LOC_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.RESOURCE_NAME_VAR_NAME;
import static de.walware.ecommons.debug.core.variables.ResourceVariables.RESOURCE_PATH_VAR_NAME;

import java.io.File;
import java.net.URI;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.debug.core.ECommonsDebugCore;
import de.walware.ecommons.debug.internal.core.Messages;


public class ResourceVariableResolver implements IDynamicVariableResolver {
	
	
	public static interface IResolveContext {
		
		
		IResource getResource();
		
	}
	
	public static class SelectedResourceContext implements IResolveContext {
		
		
		public SelectedResourceContext() {
		}
		
		@Override
		public IResource getResource() {
			final IStringVariableManager manager= VariablesPlugin.getDefault().getStringVariableManager();
			try {
				final String path= manager.performStringSubstitution("${selected_resource_path}"); //$NON-NLS-1$
				return ResourcesPlugin.getWorkspace().getRoot().findMember(path, true);
			} catch (final CoreException e) {
				return null;
			}
		}
		
	}
	
	
	private static final byte EXISTS_MASK=                  0b0_0000_1100;
	public static final byte EXISTS_NEVER=                  0b0_0000_0000;
	public static final byte EXISTS_SELECTED=               0b0_0000_0100;
	public static final byte EXISTS_ALWAYS=                 0b0_0000_1000;
	
	protected static final byte RESOURCE= 1;
	protected static final byte CONTAINER= 2;
	protected static final byte PROJECT= 3;
	
	private static final IResolveContext DEFAULT_CONTEXT= new SelectedResourceContext();
	
	
	private final IResolveContext context;
	
	private final byte flags;
	
	
	public ResourceVariableResolver() {
		this(null, EXISTS_SELECTED);
	}
	
	public ResourceVariableResolver(final IResolveContext context) {
		this(context, EXISTS_SELECTED);
	}
	
	public ResourceVariableResolver(final IResolveContext context, final byte flags) {
		this.context= (context != null) ? context : DEFAULT_CONTEXT;
		this.flags= flags;
	}
	
	
	protected IResolveContext getContext() {
		return this.context;
	}
	
	@Override
	public String resolveValue(final IDynamicVariable variable, final String argument)
			throws CoreException {
		switch (variable.getName()) {
		case RESOURCE_LOC_VAR_NAME:
			return toLocValue(variable, getResource(variable, RESOURCE, argument));
		case RESOURCE_PATH_VAR_NAME:
			return toPathValue(variable, getResource(variable, RESOURCE, argument));
		case RESOURCE_NAME_VAR_NAME:
			return toNameValue(variable, getResource(variable, RESOURCE, argument));
		case RESOURCE_ENC_VAR_NAME:
			return toEncValue(variable, getResource(variable, RESOURCE, argument));
		
		case CONTAINER_LOC_VAR_NAME:
			return toLocValue(variable, getResource(variable, CONTAINER, argument));
		case CONTAINER_PATH_VAR_NAME:
			return toPathValue(variable, getResource(variable, CONTAINER, argument));
		case CONTAINER_NAME_VAR_NAME:
			return toNameValue(variable, getResource(variable, CONTAINER, argument));
		case CONTAINER_ENC_VAR_NAME:
			return toEncValue(variable, getResource(variable, CONTAINER, argument));
		
		case PROJECT_LOC_VAR_NAME:
			return toLocValue(variable, getResource(variable, PROJECT, argument));
		case PROJECT_PATH_VAR_NAME:
			return toPathValue(variable, getResource(variable, PROJECT, argument));
		case PROJECT_NAME_VAR_NAME:
			return toNameValue(variable, getResource(variable, PROJECT, argument));
		case PROJECT_ENC_VAR_NAME:
			return toEncValue(variable, getResource(variable, PROJECT, argument));
		
		case FILE_NAME_BASE_VAR_NAME:
			return toNameBaseValue(variable, getResourcePath(variable, argument));
		case FILE_NAME_EXT_VAR_NAME:
			return toNameExtValue(variable, getResourcePath(variable, argument));
		
		default:
			throw new UnsupportedOperationException(variable.getName());
		}
	}
	
	
	protected boolean requireExists(final IDynamicVariable variable, final String argument) {
		switch (this.flags & EXISTS_MASK) {
		case EXISTS_NEVER:
			return false;
		case EXISTS_SELECTED:
			return (argument == null);
		case EXISTS_ALWAYS:
			return true;
		default:
			throw new IllegalStateException("flags= " + Integer.toBinaryString(this.flags)); //$NON-NLS-1$
		}
	}
	
	protected IResource getResource(final IDynamicVariable variable, final byte resourceType,
			final String argument) throws CoreException {
		IResource resource;
		if (argument == null) {
			resource= this.context.getResource();
			if (resource == null) {
				throw new CoreException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
						NLS.bind(Messages.ResourceVariable_error_Resource_EmptySelection_message,
								variable.getName() )));
			}
		}
		else {
			final IWorkspaceRoot root= ResourcesPlugin.getWorkspace().getRoot();
			final IPath path;
			if (!root.getFullPath().isValidPath(argument) ||
					(path= new Path(argument)).isEmpty()
					|| path.getDevice() != null) {
				throw new CoreException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
						NLS.bind(Messages.ResourceVariable_error_Resource_InvalidPath_message, 
								variable.getName(), argument )));
			}
			resource= root.findMember(path);
			if (resource == null && !requireExists(variable, argument) && path.segmentCount() > 1) {
				resource= root.getFile(path);
			}
		}
		
		if (resource != null) {
			resource= toVariableResource(variable, resourceType, resource);
		}
		if (resource == null || (requireExists(variable, argument) && !resource.exists())) {
			throw new CoreException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
					NLS.bind(Messages.ResourceVariable_error_Resource_NonExisting_message, 
							variable.getName(), argument )));
		}
		return resource;
	}
	
	protected IPath getResourcePath(final IDynamicVariable variable, final String argument)
			throws CoreException {
		if (argument == null) {
			return getResource(variable, RESOURCE, argument).getFullPath();
		}
		else {
			return new Path(argument);
		}
	}
	
	protected IResource toVariableResource(final IDynamicVariable variable, final byte resourceType,
			final IResource resource) {
		switch (resourceType) {
		case RESOURCE:
			return resource;
		case CONTAINER:
			return resource.getParent();
		case PROJECT:
			return resource.getProject();
		default:
			throw new UnsupportedOperationException(variable.getName());
		}
	}
	
	protected String toLocValue(final IDynamicVariable variable, final IResource resource)
			throws CoreException {
		final URI uri= resource.getLocationURI();
		if (uri != null) {
			final File file= EFS.getStore(uri).toLocalFile(0, null);
			if (file != null) {
				return file.getAbsolutePath();
			}
		}
		throw new CoreException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
				NLS.bind(Messages.ResourceVariable_error_Resource_InvalidPath_message,
						variable.getName(), resource.getFullPath().toString() )));
	}
	
	protected String toPathValue(final IDynamicVariable variable, final IResource resource)
			throws CoreException {
		return resource.getFullPath().toString();
	}
	
	protected String toNameValue(final IDynamicVariable variable, final IResource resource)
			throws CoreException {
		return resource.getName();
	}
	
	protected String toEncValue(final IDynamicVariable variable, final IResource resource)
			throws CoreException {
		if (resource instanceof IFile) {
			return ((IFile) resource).getCharset(true);
		}
		else { // (resource instanceof IContainer)
			return ((IContainer) resource).getDefaultCharset(true);
		}
	}
	
	protected String toNameBaseValue(final IDynamicVariable variable, final IPath path) {
		final String lastSegment= path.lastSegment();
		final String extension= path.getFileExtension();
		return (extension != null) ?
				lastSegment.substring(0, lastSegment.length() - (extension.length() + 1)) :
				lastSegment;
	}
	
	protected String toNameExtValue(final IDynamicVariable variable, final IPath path) {
		return path.getFileExtension();
	}
	
}
