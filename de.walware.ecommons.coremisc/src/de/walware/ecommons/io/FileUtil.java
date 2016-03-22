/*=============================================================================#
 # Copyright (c) 2006-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.io;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.SequenceInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.filesystem.IFileSystem;
import org.eclipse.core.filesystem.URIUtil;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.ICommonStatusConstants;
import de.walware.ecommons.io.internal.EFSUtilImpl;
import de.walware.ecommons.io.internal.WorkspaceUtilImpl;


/**
 * Utilities to work with files.
 * 
 * TODO: add write action (text stream)
 */
public abstract class FileUtil {
	
	
	public static final String UTF_8 = "UTF-8"; //$NON-NLS-1$
	
	public static final String UTF_16_BE = "UTF-16BE"; //$NON-NLS-1$
	
	public static final String UTF_16_LE = "UTF-16LE"; //$NON-NLS-1$
	
	/**
	 * Constant that identifies the Byte-Order-Mark for contents encoded with 
	 * the UTF-8 character encoding scheme. 
	 */
	public final static byte[] BOM_UTF_8 = {(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
	
	/**
	 * Constant that identifies the Byte-Order-Mark for contents encoded with 
	 * the UTF-16 Big Endian character encoding scheme. 
	 */
	public final static byte[] BOM_UTF_16BE = {(byte) 0xFE, (byte) 0xFF};
	
	/**
	 * Constant that identifies the Byte-Order-Mark for contents encoded with 
	 * the UTF-16 Little Endian character encoding scheme. 
	 */
	public final static byte[] BOM_UTF_16LE = {(byte) 0xFF, (byte) 0xFE};
	
	
/*-- Local files --*/
	public static IFileStore getLocalFileStore(final String s) throws CoreException {
		return getLocalFileStore(s, (IFileStore) null);
	}
	
	/**
	 * Resolves a string presentation of a path to an IFileStore in the local file system,
	 * if possible.
	 * 
	 * As usual the IFileStore is a resource handle only and the resource must not exists.
	 * 
	 * @param s string representation of a path
	 * @param relativeParent optional folder used as parent if path is relative
	 * @return an IFileStore in the local file system
	 * @throws CoreException if path is not a valid local path
	 */
	public static IFileStore getLocalFileStore(final String s, final IFileStore relativeParent) throws CoreException {
		if (s.length() > 0) {
			final IFileSystem localFS = EFS.getLocalFileSystem();
			if (s.startsWith(EFS.SCHEME_FILE)) {
				try {
					return localFS.getStore(new URI(s).normalize());
				}
				catch (final URISyntaxException e) {
					throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, e.getReason()));
				}
			}
			final IPath path = Path.fromOSString(s);
			if (path.isUNC()) {
				final URI uri = URIUtil.toURI(path);
				if (uri != null) {
					return localFS.getStore(uri);
				}
			}
			if (path.isAbsolute()) {
				final String device = path.getDevice();
				if (device == null || device.length() <= 2) {
//					return localFS.getStore(URIUtil.toURI(path));
					final URI uri = URIUtil.toURI(s);
					if (uri != null) {
						return localFS.getStore(uri);
					}
				}
			}
			else if (relativeParent != null && // !path.isAbsolute() &&
					path.getDevice() == null) {
				return relativeParent.getFileStore(path);
			}
		}
		else if (relativeParent != null) { // && s.length() == 0
			return relativeParent;
		}
		throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, "No local filesystem resource."));
	}
	
	/**
	 * Resolves a string presentation of a path to an IFileStore in the local file system,
	 * if possible.
	 * 
	 * As usual the IFileStore is a resource handle only and the resource must not exists.
	 * 
	 * @param s string representation of a path
	 * @param relativeParent optional folder used as parent if path is relative
	 * @return an IFileStore in the local file system
	 * @throws CoreException if path is not a valid local path
	 */
	public static IFileStore getLocalFileStore(final String s, final IContainer relativeParent) throws CoreException {
		if (s.length() > 0) {
			final IFileSystem localFS = EFS.getLocalFileSystem();
			if (s.startsWith(EFS.SCHEME_FILE)) {
				try {
					return localFS.getStore(new URI(s).normalize());
				}
				catch (final URISyntaxException e) {
					throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, e.getReason()));
				}
			}
			final IPath path = Path.fromOSString(s);
			if (path.isUNC()) {
				final URI uri = URIUtil.toURI(path);
				if (uri != null) {
					return localFS.getStore(uri);
				}
			}
			if (path.isAbsolute()) {
				final String device = path.getDevice();
				if (device == null || device.length() <= 2) {
//					return localFS.getStore(URIUtil.toURI(path));
					final URI uri = URIUtil.toURI(s);
					if (uri != null) {
						return localFS.getStore(uri);
					}
				}
			}
			else if (relativeParent != null && // !path.isAbsolute() &&
					path.getDevice() == null) {
				return EFS.getStore(relativeParent.getFile(path).getLocationURI().normalize());
			}
		}
		else if (relativeParent != null) { // && s.length() == 0
			return EFS.getStore(relativeParent.getLocationURI().normalize());
		}
		throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, "No local filesystem resource."));
	}
	
	/**
	 * Resolves a string presentation of a path to an IFileStore in the local file system,
	 * if possible.
	 * 
	 * As usual the IFileStore is a resource handle only and the resource must not exists.
	 * 
	 * @param s string representation of a path
	 * @return an IFileStore in the local file system
	 * @throws CoreException if path is not a valid local path
	 */
	public static IFileStore getFileStore(final String location) throws CoreException {
		return getFileStore(location, null);
	}
	
	/**
	 * Resolves a string presentation of a path to an IFileStore, if possible.
	 * 
	 * The resource must not be in the local file system (URI with scheme).
	 * As usual the IFileStore is a resource handle only and the resource must not exists.
	 * 
	 * @param s string representation of a path
	 * @param relativeParent optional folder used as parent if path is relative
	 * @return an IFileStore in the local file system
	 * @throws CoreException if path is not a valid local path
	 */
	public static IFileStore getFileStore(final String location, final IFileStore relativeParent) throws CoreException {
		try {
			return FileUtil.getLocalFileStore(location, relativeParent);
		}
		catch (final CoreException e) {
		}
		
		final int p = location.indexOf(':');
		if (p > 1) {
			try {
				final URI uri = new URI(location);
				if (uri.getScheme() != null) {
					return EFS.getStore(uri);
				}
			}
			catch (final URISyntaxException e) {
				// use uri error message only if we find valid scheme at the beginning
				try {
					new URI(location.substring(0, p), "a", null); //$NON-NLS-1$
					throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, e.getReason()));
				}
				catch (final URISyntaxException invalidScheme) {
				}
			}
		}
		
		throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, "No supported filesystem resource."));
	}
	
	/**
	 * Returns a string for the given file store.
	 * The string can be converted back to a file store by the methods
	 * {@link #getFileStore(String)} etc. of this class.
	 * 
	 * @param fileStore the file store or <code>null</code>
	 * @return the string for the file store or <code>null</code>, if the file store was <code>null</code>
	 */
	public static String toString(final IFileStore fileStore) {
		if (fileStore == null) {
			return null;
		}
		if (fileStore.getFileSystem().getScheme().equals(EFS.SCHEME_FILE)) {
			return fileStore.toString();
		}
		else {
			return fileStore.toURI().toString();
		}
	}
	
	
	/**
	 * Tries to resolves string to local file store handler.
	 *  - Performs Variable substitution
	 * 
	 * @param location path as string of the location
	 * @param parent optional parent directory, used if <code>location</code> is relative
	 * @param child optional child, appended to location
	 * @return the file store handler
	 * @throws CoreException
	 */
	public static IFileStore expandToLocalFileStore(final String location, final IFileStore parent, final String child) throws CoreException {
		final IStringVariableManager variables = VariablesPlugin.getDefault().getStringVariableManager();
		final String expanded = variables.performStringSubstitution(location);
		final IFileStore localFileStore = getLocalFileStore(expanded, parent);
		if (child != null) {
			return localFileStore.getChild(child);
		}
		return localFileStore;
	}
	
	public static IFile getAsWorkspaceFile(final URI uri) {
		final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = URIUtil.toPath(uri);
		if (path != null) {
			path = path.makeAbsolute();
			final IFile file = root.getFileForLocation(path);
			if (file != null) {
				return file;
			}
		}
		final IFile[] files = root.findFilesForLocationURI(uri);
		if (files.length > 0) {
			return files[0];
		}
		return null;
	}
	
	public static void prepareTextOutput(final OutputStream outputStream, final String encoding) throws IOException {
		if (encoding == null) {
			return;
		}
		final byte[] prefix;
		if (encoding.equals(FileUtil.UTF_8)) {
			prefix = FileUtil.BOM_UTF_8;
		}
		else if (encoding.equals(FileUtil.UTF_16_BE)) {
			prefix = FileUtil.BOM_UTF_16BE;
		}
		else if (encoding.equals(FileUtil.UTF_16_LE)) {
			prefix = FileUtil.BOM_UTF_16LE;
		}
		else {
			return;
		}
		for (int i = 0; i < prefix.length; i++) {
			outputStream.write(prefix[i]);
		}
	}
	
	
/*-- File Operations --*/
	
	public static abstract class AbstractFileOperation {
		
		protected int fMode = EFS.NONE;
		
		protected String fCharset = UTF_8;
		protected boolean fForceCharset = false;
		
		protected AbstractFileOperation() {
		}
		
		public void setFileOperationMode(final int mode) {
			fMode = mode;
		}
		
		public void setCharset(final String charset, final boolean forceCharset) {
			fCharset = charset;
			fForceCharset = forceCharset;
		}
		
		
		public void doOperation(final IProgressMonitor monitor) throws CoreException, OperationCanceledException {
			runInEnv(monitor);
		}
		
		protected abstract void runInEnv(IProgressMonitor monitor) throws CoreException, OperationCanceledException;
		
		protected void runAsWorkspaceRunnable(final IProgressMonitor monitor, final ISchedulingRule rule) throws CoreException, OperationCanceledException {
			final IWorkspace workspace = ResourcesPlugin.getWorkspace();
			final IWorkspaceRunnable workspaceRunner = new IWorkspaceRunnable() {
				@Override
				public void run(final IProgressMonitor monitor) throws CoreException {
					runInEnv(monitor);
				}
			};
			workspace.run(workspaceRunner, rule, IWorkspace.AVOID_UPDATE, monitor);
		}
	}
	
	public abstract class WriteTextFileOperation extends AbstractFileOperation {
		
		protected WriteTextFileOperation() {
			super();
		}
		
		protected abstract void writeImpl(IProgressMonitor monitor) throws CoreException, UnsupportedEncodingException, IOException;
		
		@Override
		protected void runInEnv(final IProgressMonitor monitor) throws CoreException, OperationCanceledException {
			try {
				monitor.beginTask("Writing to "+getLabel(), 100);
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				
				writeImpl(monitor);
			}
			catch (final UnsupportedEncodingException e) {
				throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"The selected charset is unsupported on your system.", e));
			}
			catch (final IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"Error while writing to file.", e));
			}
			finally {
				monitor.done();
			}
		}
	}
	
	
	public static interface ReaderAction {
		
		void run(BufferedReader reader, IProgressMonitor monitor) throws IOException, CoreException;
		
	}
	
	protected static class FileInput implements Closeable {
		
		private String fEncoding;
		private String fDefaultEncoding;
		private InputStream fStream;
		
		public FileInput(final InputStream input, final String expliciteCharsetHint) throws IOException, CoreException {
			fStream = input;
			if (expliciteCharsetHint != null) {
				if (expliciteCharsetHint.equals(UTF_8)
						|| expliciteCharsetHint.equals(UTF_16_BE)
						|| expliciteCharsetHint.equals(UTF_16_LE)) {
					read(input);
				}
				fDefaultEncoding = expliciteCharsetHint;
			}
			else {
				read(input);
			}
			fEncoding = (fDefaultEncoding != null) ? fDefaultEncoding : FileUtil.UTF_8;
		}
		
		void read(final InputStream input) throws IOException {
			try {
				final int n = 3;
				final byte[] bytes = new byte[n];
				final int readed = input.read(bytes, 0, n);
				if (readed == 0) {
					return;
				}
				int next = 0;
				if (startsWith(bytes, BOM_UTF_8)) {
					next = BOM_UTF_8.length;
					fDefaultEncoding = FileUtil.UTF_8;
				}
				else if (startsWith(bytes, BOM_UTF_16BE)) {
					next = BOM_UTF_16BE.length;
					fDefaultEncoding = FileUtil.UTF_16_BE;
				}
				else if (startsWith(bytes, BOM_UTF_16LE)) {
					next = BOM_UTF_16LE.length;
					fDefaultEncoding = FileUtil.UTF_16_LE;
				}
				if (readed-next > 0) {
					fStream = new SequenceInputStream(new ByteArrayInputStream(
							bytes, next, readed-next), input);
				}
			}
			catch (final IOException e) {
				saveClose(input);
				throw e;
			}
		}
		
		private boolean startsWith(final byte[] array, final byte[] start) {
			for (int i = 0; i < start.length; i++) {
				if (array[i] != start[i]) {
					return false;
				}
			}
			return true;
		}
		
		public void setEncoding(final String encoding, final boolean force) {
			if (encoding == null && fDefaultEncoding != null) {
				fEncoding = fDefaultEncoding;
			}
			if (force || fDefaultEncoding == null) {
				fEncoding = encoding;
			}
		}
		
		@Override
		public void close() throws IOException {
			if (fStream != null) {
				fStream.close();
			}
		}
		
		public String getDefaultCharset() {
			return fDefaultEncoding;
		}
		
		public Reader getReader() throws UnsupportedEncodingException {
			return new InputStreamReader(fStream, fEncoding);
		}
		
	}
	
	public abstract class ReadTextFileOperation extends AbstractFileOperation {
		
		protected abstract FileInput getInput(IProgressMonitor monitor) throws CoreException, IOException;
		protected abstract ReaderAction getAction();
		
		public ReadTextFileOperation() {
			super();
		}
		
		@Override
		protected void runInEnv(final IProgressMonitor monitor) throws CoreException {
			FileInput fi = null;
			BufferedReader reader = null;
			try {
				monitor.beginTask(null, 100);
				final String fileLabel = getLabel();
				monitor.subTask("Opening "+fileLabel+"...");
				if (monitor.isCanceled()) {
					throw new OperationCanceledException();
				}
				
				fi = getInput(new SubProgressMonitor(monitor, 10));
				fi.setEncoding(fCharset, fForceCharset);
				
				reader = new BufferedReader(fi.getReader());
				monitor.worked(5);
				monitor.subTask("Reading "+fileLabel+"...");
				getAction().run(reader, new SubProgressMonitor(monitor, 80));
			}
			catch (final UnsupportedEncodingException e) {
				throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"The selected charset is unsupported on your system.", e));
			}
			catch (final IOException e) {
				throw new CoreException(new Status(IStatus.ERROR, ECommons.PLUGIN_ID, ICommonStatusConstants.IO_ERROR,
						"Error while reading the file.", e));
			}
			finally {
				saveClose(reader);
				saveClose(fi);
				monitor.done();
			}
		}
	}
	
	public static FileUtil getFileUtil(final Object file) {
		if (file == null) {
			throw new NullPointerException("file");
		}
		if (file instanceof IFile) {
			return new WorkspaceUtilImpl((IFile) file);
		}
		else if (file instanceof IFileStore) {
			final IFileStore efsFile = (IFileStore) file;
			final IFile iFile = getAsWorkspaceFile(efsFile.toURI());
			if (iFile != null) {
				return new WorkspaceUtilImpl(iFile);
			}
			return new EFSUtilImpl(efsFile);
		}
		throw new IllegalArgumentException("Unknown file object: " + file.getClass());
	}
	
	
	protected static void saveClose(final Closeable stream) {
		if (stream != null) {
			try {
				stream.close();
			} catch (final IOException e) {}
		}
	}
	
	
	/**
	 * Return the time stamp of the file
	 * 
	 * @param monitor a progress monitor for progress and cancellation report
	 * @return the time stamp
	 * @throws CoreException
	 */
	public abstract long getTimeStamp(IProgressMonitor monitor) throws CoreException;
	
	/**
	 * Return a label for the file. It can be used to show it in the UI, but not to access or
	 * identify the file programatically.
	 * 
	 * @param monitor a progress monitor for progress and cancellation report
	 * @return the label
	 * @throws CoreException
	 */
	public abstract String getLabel();
	
	/**
	 * Return the URI of the file.
	 * 
	 * @return the URI or <code>null</code> if the file doesn't have an URI
	 * @throws CoreException
	 */
	public abstract URI getURI();
	
	
	public abstract ReadTextFileOperation createReadTextFileOp(ReaderAction action);
	public abstract WriteTextFileOperation createWriteTextFileOp(String content);
	
}
