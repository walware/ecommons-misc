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

package de.walware.ecommons.io;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileInfo;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.coreutils.internal.CoreMiscellanyPlugin;
import de.walware.ecommons.io.internal.Messages;
import de.walware.ecommons.runtime.core.util.StatusUtils;
import de.walware.ecommons.variables.core.VariableText2;
import de.walware.ecommons.variables.core.VariableUtils;


/**
 * A configurable resource validator.
 * 
 * Validates <code>String</code> (with variables) representing a local file path
 * or a URI and file handles of type <code>IFileStore</code> and
 * <code>IResource</code> (for Workspace resources).
 */
public class FileValidator implements IValidator {
	
	
	private Object fExplicitObject;
	private boolean fInCheck = false;
	
	private IResource fWorkspaceResource;
	private IFileStore fFileStore;
	
	private IStatus status;
	
	private String fResourceLabel;
	
	private VariableText2 variableResolver;
	
	private int fOnEmpty;
	private int fOnNotExisting;
	private int fOnExisting;
	private VariableText2.Severities onVariableProblems;
	private int fOnFile;
	private int fOnDirectory;
	private int fOnNotLocal;
	private boolean fIgnoreRelative;
	private IStringVariable fRelativePrefix;
	private int fRelativeMax = -1;
	private IPath fRelativePath;
	private boolean fRequireWorkspace;
	private boolean fAsWorkspacePath;
	private Map<Pattern, Integer> fOnPattern;
	
	private IValidator fFileStoreValidator;
	
	private int fCurrentMax;
	
	
	/**
	 * 
	 */
	public FileValidator() {
		fOnNotExisting = IStatus.OK;
		fOnExisting = IStatus.OK;
		fOnEmpty = IStatus.ERROR;
		this.onVariableProblems= VariableText2.Severities.RESOLVE;
		fOnFile = IStatus.OK;
		fOnDirectory = IStatus.OK;
		fOnNotLocal = IStatus.ERROR;
		fIgnoreRelative = false;
	}
	
	/**
	 * New validator initialized with specified default mode
	 * ({@link #setDefaultMode(boolean)})
	 */
	public FileValidator(final boolean existingResource) {
		this();
		setDefaultMode(existingResource);
	}
	
	public void setDefaultMode(final boolean existingResource) {
		fOnNotExisting = (existingResource) ? IStatus.ERROR : IStatus.OK;
		fOnExisting = (existingResource) ? IStatus.OK : IStatus.WARNING;
	}
	
	
	void checkVariable(final IStringVariable variable) {
	}
	
	
	public void setOnEmpty(final int severity) {
		fOnEmpty = severity;
		resetResolution();
	}
	public int getOnEmpty() {
		return fOnEmpty;
	}
	
	public void setOnExisting(final int severity) {
		fOnExisting = severity;
		resetResolution();
	}
	public int getOnExisting() {
		return fOnExisting;
	}
	
	public void setOnNotExisting(final int severity) {
		fOnNotExisting = severity;
		resetResolution();
	}
	public int getOnNotExisting() {
		return fOnNotExisting;
	}
	
	public void setOnLateResolve(final int severity) {
		if (severity != this.onVariableProblems.getUnresolved()) {
			this.onVariableProblems= new VariableText2.Severities(IStatus.ERROR, severity);
		}
		resetResolution();
	}
	public int getOnLateResolve() {
		return this.onVariableProblems.getUnresolved();
	}
	
	public void setOnFile(final int severity) {
		fOnFile = severity;
		resetResolution();
	}
	public int getOnFile() {
		return fOnFile;
	}
	
	public void setOnDirectory(final int severity) {
		fOnDirectory = severity;
		resetResolution();
	}
	public int getOnDirectory() {
		return fOnDirectory;
	}
	
	public void setOnNotLocal(final int severity) {
		fOnNotLocal = severity;
		resetResolution();
	}
	public int getOnNotLocal() {
		return fOnNotLocal;
	}
	public void setIgnoreRelative(final boolean ignore) {
		fRelativeMax = -1;
		fIgnoreRelative = ignore;
		resetResolution();
	}
	
	public void setRelative(final IStringVariable prefix, final int maxSeverity) {
		fRelativePrefix = prefix;
		fRelativeMax = maxSeverity;
		fIgnoreRelative = false;
		
		checkVariable(prefix);
		resetResolution();
	}
	
	protected String getRelativePrefix() {
		String prefix= null;
		if (fRelativePrefix != null) {
			try {
				prefix= VariableUtils.getValue(fRelativePrefix);
			}
			catch (final CoreException e) {
			}
		}
		if (prefix != null && !prefix.endsWith("/") && !prefix.endsWith("\\")) { //$NON-NLS-1$ //$NON-NLS-2$
			prefix+= '/';
		}
		return prefix;
	}
	
	public void setRequireWorkspace(final boolean require, final boolean wsPath) {
		fRequireWorkspace = require;
		if (require) {
			fAsWorkspacePath = wsPath;
		}
		resetResolution();
	}
	
	public void setVariableResolver(final VariableText2 variableResolver) {
		this.variableResolver= variableResolver;
		
		if (variableResolver.getExtraVariables() != null) {
			for (final IStringVariable aVariable : variableResolver.getExtraVariables().values()) {
				checkVariable(aVariable);
			}
		}
		updateVariableResolution();
	}
	
	public VariableText2 getVariableResolver() {
		return this.variableResolver;
	}
	
	public void updateVariableResolution() {
		if (fExplicitObject instanceof String || fRelativePrefix != null) {
			resetResolution();
		}
	}
	
	/**
	 * @param pattern pattern
	 * @param severity at moment only OK_STATUS or -1
	 */
	public void setOnPattern(final Pattern pattern, final int severity) {
		if (fOnPattern == null) {
			fOnPattern = new LinkedHashMap<>();
		}
		if (severity >= 0) {
			fOnPattern.put(pattern, severity);
		}
		else {
			fOnPattern.remove(pattern);
		}
	}
	public int getOnPattern(final Pattern pattern) {
		if (fOnPattern != null) {
			final Integer integer = fOnPattern.get(pattern);
			if (integer != null) {
				return integer.intValue();
			}
		}
		return -1;
	}
	
	public void setFileStoreValidator(final IValidator validator) {
		fFileStoreValidator = validator;
	}
	
	public void setResourceLabel(final String label) {
		fResourceLabel= label;
	}
	
	
	/**
	 * Sets explicitly the object to validate.
	 * A <code>null</code> value stops the explicit mode.  If the value is set
	 * explicitly, the value specified in the validate(...) methods is ignored.
	 * @param value the resource to validate or <code>null</code>.
	 */
	public void setExplicit(final Object value) {
		fFileStore = null;
		fWorkspaceResource = null;
		fExplicitObject = value;
		setStatus(null);
	}
	
	private void resetResolution() {
		fFileStore = null;
		fWorkspaceResource = null;
		setStatus(null);
	}
	
	protected void setStatus(final IStatus status) {
		this.status= status;
	}
	
	@Override
	public IStatus validate(final Object value) {
		if (!checkExplicit()) {
			doValidateChecked(value);
		}
		return this.status;
	}
	
	boolean checkExplicit() {
		if (fExplicitObject != null) {
			if (this.status == null) {
				doValidateChecked(fExplicitObject);
			}
			return true;
		}
		return false;
	}
	
	private void doValidateChecked(final Object value) {
		if (!fInCheck) {
			fInCheck = true;
			try {
				IStatus status = doValidate1(value);
				if (status.getSeverity() < IStatus.ERROR && fFileStoreValidator != null) {
					final IStatus status2 = fFileStoreValidator.validate(getFileStore());
					if (status2 != null && status2.getSeverity() > status.getSeverity()) {
						status = status2;
					}
				}
				setStatus(status);
			}
			catch (final Exception e) {
				CoreMiscellanyPlugin.getDefault().log(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, -1,
						NLS.bind("An error occurred when validating resource path ({0}).", value), e));
			}
			finally {
				fInCheck = false;
			}
		}
	}
	
	private IStatus doValidate1(Object value) {
		fFileStore = null;
		fWorkspaceResource = null;
		fRelativePath = null;
		fCurrentMax = Integer.MAX_VALUE;
		
		// Resolve string
		if (value instanceof IPath) {
			value = ((IPath) value).toOSString();
		}
		if (value instanceof String) {
			String s = (String) value;
			if (s.length() == 0) {
				return createStatus(fOnEmpty,
						Messages.Resource_error_NoInput_message, Messages.Resource_error_NoInput_message_0,
						null );
			}
			if (fOnPattern != null && !fOnPattern.isEmpty()) {
				for (final Entry<Pattern, Integer> entry : fOnPattern.entrySet()) {
					if (entry.getKey().matcher(s).find()) {
						return Status.OK_STATUS;
					}
				}
			}
			try {
				s = resolveExpression(s);
			} catch (final CoreException e) {
				return createStatus(e.getStatus().getSeverity(),
						Messages.Resource_error_Other_message, Messages.Resource_error_Other_message_0,
						e.getStatus().getMessage() );
			}
			if (s.length() == 0) {
				return createStatus(fOnEmpty,
						Messages.Resource_error_NoInput_message, Messages.Resource_error_NoInput_message_0,
						null );
			}
			{	final IPath path = new Path(s);
				if (!path.isAbsolute()) {
					fRelativePath = path;
					if (fRelativeMax >= 0 && fRelativeMax < fCurrentMax) {
						fCurrentMax = fRelativeMax;
					}
					final String prefix= getRelativePrefix();
					if (prefix != null) {
						s= prefix + s;
					}
					else if (fIgnoreRelative) {
						return Status.OK_STATUS;
					}
				}
			}
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			if (fAsWorkspacePath) {
				final int typeMask = ((fOnFile < IStatus.ERROR) ? IResource.FILE : 0) |
						((fOnDirectory < IStatus.ERROR) ? (IResource.PROJECT | IResource.FOLDER) : 0);
				final IStatus status = workspace.validatePath(s, typeMask);
				if (!status.isOK()) {
					return createStatus(status.getSeverity(),
							Messages.Resource_error_Other_message, Messages.Resource_error_Other_message_0,
							status.getMessage() );
				}
				final IPath path = new Path(s);
				fWorkspaceResource = workspace.getRoot().findMember(path, true);
				if (fWorkspaceResource == null) {
					final IResource project = workspace.getRoot().findMember(path.segment(0), true);
					if (project == null) {
						return createStatus(IStatus.ERROR,
								Messages.Resource_error_NotInWorkspace_message, Messages.Resource_error_NotInWorkspace_message_0,
								null );
					}
					if (path.segmentCount() == 1 && fOnDirectory < IStatus.ERROR) {
						fWorkspaceResource = project;
					}
					else if (fOnDirectory < fOnFile){
						fWorkspaceResource = workspace.getRoot().getFolder(path);
					}
					else {
						fWorkspaceResource = workspace.getRoot().getFile(path);
					}
				}
			}
			else {
				// search efs reference
				try {
					fFileStore = FileUtil.getFileStore(s);
					if (fFileStore == null) {
						return createStatus(IStatus.ERROR,
								Messages.Resource_error_NoValidSpecification_message, Messages.Resource_error_NoValidSpecification_message_0,
								null );
					}
				}
				catch (final CoreException e) {
					return createStatus(IStatus.ERROR,
							Messages.Resource_error_NoValidSpecification_message, Messages.Resource_error_NoValidSpecification_message_0,
							e.getStatus().getMessage() );
				}
				
				// search file in workspace 
				if (fFileStore != null) {
					final IResource[] resources = (fFileStore.fetchInfo().isDirectory()) ? 
							workspace.getRoot().findContainersForLocationURI(fFileStore.toURI()) :
							workspace.getRoot().findFilesForLocationURI(fFileStore.toURI());
					if (resources.length > 0) {
						fWorkspaceResource = resources[0];
					}
				}
			}
		}
		
		if (value instanceof IFileStore) {
			fFileStore = (IFileStore) value;
		}
		else if (value instanceof IResource) {
			fWorkspaceResource = (IResource) value;
		}
		
		if (!fRequireWorkspace && fFileStore != null) {
			return validateFileStore();
		}
		else if (fWorkspaceResource != null) {
			return validateWorkspaceResource();
		}
		else if (fRequireWorkspace) {
			return createStatus(IStatus.ERROR,
					Messages.Resource_error_NotInWorkspace_message, Messages.Resource_error_NotInWorkspace_message_0,
					null );
		}
		else {
			throw new IllegalArgumentException();
		}
	}
	
	protected String resolveExpression(final String expression) throws CoreException {
		if (this.variableResolver != null) {
			return this.variableResolver.performStringSubstitution(expression,
					this.onVariableProblems );
		}
		
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		try {
			return manager.performStringSubstitution(expression);
		}
		catch (final CoreException e) {
			manager.validateStringVariables(expression); // throws invalid variable
			throw new CoreException(new Status(getOnLateResolve(), e.getStatus().getPlugin(), e.getStatus().getMessage())); // throws runtime variable
		}
	}
	
	private IResource findWorkspaceResource(final URI location) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource[] found = null;
		if (fOnFile != IStatus.ERROR) {
			found = root.findFilesForLocationURI(location);
		}
		if ((found == null || found.length == 0)
				&& fOnDirectory != IStatus.ERROR) {
			found = root.findContainersForLocationURI(location);
		}
		if (found != null && found.length > 0) {
			return found[0];
		}
		return null;
	}
	
	protected IStatus validateWorkspaceResource() {
		IStatus status = Status.OK_STATUS;
		if (fOnNotLocal != IStatus.OK) {
			if (!isLocalFile()) {
				status = createStatus(fOnNotLocal,
						Messages.Resource_error_NotLocal_message, Messages.Resource_error_NotLocal_message_0,
						null );
			}
			if (status.getSeverity() == IStatus.ERROR) {
				return status;
			}
		}
		if (fOnExisting != IStatus.OK || fOnNotExisting != IStatus.OK || fOnFile != IStatus.OK || fOnDirectory != IStatus.OK) {
			status = StatusUtils.getMoreSevere(status,
					createExistsStatus(fWorkspaceResource.exists(), (fWorkspaceResource instanceof IContainer)) );
		}
		return status;
	}
	
	protected IStatus validateFileStore() {
		IStatus status = Status.OK_STATUS;
		if (fOnNotLocal != IStatus.OK) {
			if (!isLocalFile()) {
				status = createStatus(fOnNotLocal,
						Messages.Resource_error_NotLocal_message, Messages.Resource_error_NotLocal_message_0,
						null );
			}
			if (status.getSeverity() == IStatus.ERROR) {
				return status;
			}
		}
		if (fOnExisting != IStatus.OK || fOnNotExisting != IStatus.OK) {
			final IFileInfo info = fFileStore.fetchInfo();
			status = StatusUtils.getMoreSevere(status,
					createExistsStatus(info.exists(), info.isDirectory()) );
		}
		return status;
	}
	
	private IStatus createExistsStatus(final boolean exists, final boolean isDirectory) {
		if (exists) {
			IStatus status = createStatus(fOnExisting,
					Messages.Resource_error_AlreadyExists_message, Messages.Resource_error_AlreadyExists_message_0,
					null );
			if (status.getSeverity() < fOnDirectory && isDirectory) {
				status = createStatus(fOnDirectory,
						Messages.Resource_error_IsDirectory_message, Messages.Resource_error_IsDirectory_message_0,
						null );
			}
			if (status.getSeverity() < fOnFile && !isDirectory) {
				status = createStatus(fOnFile,
						Messages.Resource_error_IsFile_message, Messages.Resource_error_IsFile_message_0,
						null );
			}
			return status;
		}
		else {
			return createStatus(fOnNotExisting,
					Messages.Resource_error_DoesNotExists_message, Messages.Resource_error_DoesNotExists_message_0,
					null );
		}
	}
	
	protected IStatus createStatus(int severity, final String message, String message0,
			String detail) {
		if (severity == IStatus.OK) {
			return Status.OK_STATUS;
		}
		if (severity > fCurrentMax) {
			severity = fCurrentMax;
		}
		if (detail == null) {
			detail = ""; //$NON-NLS-1$
		}
		return new Status(severity, ECommons.PLUGIN_ID, (fResourceLabel != null) ?
				NLS.bind(message, fResourceLabel, detail) :
				NLS.bind(message0, detail) );
	}
	
	
	public IFileStore getFileStore() {
		checkExplicit();
		if (fFileStore == null && fWorkspaceResource != null) {
			try {
				fFileStore = EFS.getStore(fWorkspaceResource.getLocationURI());
			} catch (final CoreException e) {
			}
		}
		return fFileStore;
	}
	
	public IResource getWorkspaceResource() {
		checkExplicit();
		if (fWorkspaceResource == null && fFileStore != null) {
			fWorkspaceResource = findWorkspaceResource(fFileStore.toURI());
		}
		return fWorkspaceResource;
	}
	
	public boolean isLocalFile() {
		final IFileStore fileStore = getFileStore();
		if (fileStore != null) {
			return fileStore.getFileSystem().equals(EFS.getLocalFileSystem());
		}
		return false;
	}
	
	public boolean isRelativeFile() {
		return (fRelativePath != null);
	}
	
	public IPath getRelativeFile() {
		return fRelativePath;
	}
	
	public IStatus getStatus() {
		checkExplicit();
		return this.status;
	}
	
}
