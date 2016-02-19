/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.util;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ibm.icu.text.DateFormat;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchDelegate;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.osgi.util.NLS;

import de.walware.jcommons.collections.CaseInsensitiveMap;

import de.walware.ecommons.debug.core.ECommonsDebugCore;
import de.walware.ecommons.debug.internal.core.Messages;


/**
 * Methods for common task when working with launch configurations and processes.
 */
public class LaunchUtils {
	
	
	public static ILaunchConfiguration findLaunchConfiguration(
			final ILaunchConfiguration[] launchConfigurations,
			final ILaunchConfigurationType type, final String name) throws CoreException {
		for (int i= 0; i < launchConfigurations.length; i++) {
			final ILaunchConfiguration aConfig= launchConfigurations[i];
			if (aConfig.getType().equals(type) && aConfig.getName().equals(name)) {
				return aConfig;
			}
		}
		return null;
	}
	
	public static ILaunchConfigurationDelegate getLaunchConfigurationDelegate(
			final ILaunchConfiguration configuration, final String mode,
			final MultiStatus status) throws CoreException {
		if (configuration == null) {
			throw new NullPointerException("configuration"); //$NON-NLS-1$
		}
		if (mode == null) {
			throw new NullPointerException("mode"); //$NON-NLS-1$
		}
		final Set<String> modes= Collections.singleton(mode);
		ILaunchDelegate launchDelegate= configuration.getPreferredDelegate(modes);
		if (launchDelegate != null) {
			return launchDelegate.getDelegate();
		}
		final ILaunchConfigurationType type= configuration.getType();
		launchDelegate= configuration.getType().getPreferredDelegate(modes);
		if (launchDelegate != null) {
			return launchDelegate.getDelegate();
		}
		final ILaunchDelegate[] delegates= type.getDelegates(modes);
		if (delegates.length > 0) {
			if (status != null && delegates.length > 1) {
				status.add(new Status(IStatus.WARNING, ECommonsDebugCore.PLUGIN_ID,
						NLS.bind("Multiple launchers available but no set as preferred for {0} ({1}).",
								type.getName(), getModeLabel(mode) )));
			}
			return delegates[0].getDelegate();
		}
		throw new CoreException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
				NLS.bind("No launcher available for {0} ({1}).",
						type.getName(), getModeLabel(mode) )));
	}
	
	private static String getModeLabel(final String mode) {
		try {
			return DebugPlugin.getDefault().getLaunchManager().getLaunchMode(mode).getLabel();
		}
		catch (final Exception e) {
			return '<' + mode + '>';
		}
	}
	
	
	public static void configureEnvironment(final Map<String, String> env, final ILaunchConfiguration configuration, final Map<String, String> add)
			throws CoreException {
		env.clear();
		env.putAll(createEnvironment(configuration, new Map[] { add }));
	}
	
	/**
	 * Adds environment variables specified in launch configuration to the map.
	 * Explicit specified variables replaces values already configured, but not
	 * appended variables from OS.
	 * 
	 * @param configuration
	 * @param environment
	 * @throws CoreException
	 */
	public static Map<String, String> createEnvironment(final ILaunchConfiguration configuration, final Map<String, String>[] add)
			throws CoreException {
		final Map<String, String> envp= (Platform.getOS().startsWith("win")) ? //$NON-NLS-1$
				new CaseInsensitiveMap<String>(64) : new HashMap<String, String>(64);
		if (configuration == null || configuration.getAttribute(ILaunchManager.ATTR_APPEND_ENVIRONMENT_VARIABLES, true)) {
			envp.putAll(DebugPlugin.getDefault().getLaunchManager().getNativeEnvironmentCasePreserved());
		}
		
		Map<String, String> custom= (configuration != null) ? configuration.getAttribute(ILaunchManager.ATTR_ENVIRONMENT_VARIABLES, (Map<String, String>) null) : null;
		if (add != null) {
			for (int i= 0; i < add.length; i++) {
				if (add[i] != null) {
					envp.putAll(check(envp, add[i]));
				}
				else if (custom != null) {
					envp.putAll(check(envp, custom));
					custom= null;
				}
			}
		}
		if (custom != null) {
			envp.putAll(check(envp, custom));
			custom= null;
		}
		
		return envp;
	}
	
	private static Pattern ENV_PATTERN= Pattern.compile("\\Q${env_var:\\E([^\\}]*)\\}"); //$NON-NLS-1$
	
	private static Map<String, String> check(final Map<String,String> current, final Map<String,String> add) throws CoreException {
		final Map<String, String> resolved= new HashMap<>();
		final Set<Entry<String, String>> entries= add.entrySet();
		for (final Entry<String, String> entry : entries) {
			String value= entry.getValue();
			if (value != null && value.length() > 0) {
				if (value.contains("${env_var:")) { //$NON-NLS-1$
					final StringBuffer sb= new StringBuffer(value.length()+32);
					final Matcher matcher= ENV_PATTERN.matcher(value);
					while (matcher.find()) {
						final String var= matcher.group(1);
						final String varValue= current.get(var);
						matcher.appendReplacement(sb, (varValue != null) ? Matcher.quoteReplacement(varValue) : ""); //$NON-NLS-1$
					}
					matcher.appendTail(sb);
					value= sb.toString();
				}
				if (value.contains("${")) { //$NON-NLS-1$
					value= VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(value, true);
				}
			}
			resolved.put(entry.getKey(), value);
		}
		return resolved;
	}
	
	
	public static String[] getProcessArguments(final ILaunchConfiguration configuration,
			final String attr) throws CoreException {
		
		final String args= configuration.getAttribute(attr, ""); //$NON-NLS-1$
		final String expanded= VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(args);
		return DebugPlugin.parseArguments(expanded);
	}
	
	private static final Pattern DOUBLE_QUOTE_PATTERN= Pattern.compile(Pattern.quote("\""));  //$NON-NLS-1$
	private static final String DOUBLE_QUOTE_REPLACEMENT= Matcher.quoteReplacement("\\\"");  //$NON-NLS-1$
	
	/**
	 * Creates UI presentation of command line (command string for shell).
	 */
	public static String generateCommandLine(final List<String> commandLine) {
		final StringBuilder builder= new StringBuilder();
		for (final String arg : commandLine) {
			DOUBLE_QUOTE_PATTERN.matcher(arg).replaceAll(DOUBLE_QUOTE_REPLACEMENT);
			if (arg.indexOf(' ') >= 0) {
				builder.append('\"');
				builder.append(arg);
				builder.append('\"');
			} else {
				builder.append(arg);
			}
			builder.append(' ');
		}
		if (builder.length() > 0) {
			return builder.substring(0, builder.length()-1);
		}
		return ""; //$NON-NLS-1$
	}
	
	
	public static String[] toKeyValueStrings(final Map<String, String> map) {
		final String[] array= new String[map.size()];
		final Iterator<Map.Entry<String, String>> iter= map.entrySet().iterator();
		for (int i= 0; i < array.length; i++) {
			final Entry<String, String> entry= iter.next();
			array[i]= entry.getKey() + '=' + entry.getValue();
		}
		return array;
	}
	
	
	public static String createProcessTimestamp(final long time) {
		return "(" + DateFormat.getDateTimeInstance().format(new Date(time)) + ")"; //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	public static String createLaunchPrefix(final ILaunchConfiguration config) {
		final StringBuilder s= new StringBuilder();
		if (config != null) {
			String type= null;
			try {
				type= config.getType().getName();
			} catch (final CoreException e) {}
			s.append(config.getName());
			if (type != null) {
				s.append(" ["); //$NON-NLS-1$
				s.append(type);
				s.append("]"); //$NON-NLS-1$
			}
		}
		else {
			s.append("[-]"); //$NON-NLS-1$
		}
		return s.toString();
	}
	
	
	public static SubMonitor initProgressMonitor(final ILaunchConfiguration configuration,
			final IProgressMonitor monitor, final int taskTotalWork) {
		final SubMonitor progress= SubMonitor.convert(monitor, Messages.LaunchDelegate_LaunchingTask_label, taskTotalWork);
		progress.subTask(Messages.LaunchDelegate_Init_subtask);
		return progress;
	}
	
	
	private LaunchUtils() {}
	
}
