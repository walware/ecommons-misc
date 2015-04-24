/*=============================================================================#
 # Copyright (c) 2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.debug.core.variables;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;

import de.walware.ecommons.collections.ImCollections;
import de.walware.ecommons.collections.ImList;
import de.walware.ecommons.variables.core.DynamicVariable;


public class ResourceVariables {
	
	
	public static final String RESOURCE_LOC_VAR_NAME= "resource_loc"; //$NON-NLS-1$
	public static final String RESOURCE_PATH_VAR_NAME= "resource_path"; //$NON-NLS-1$
	public static final String RESOURCE_NAME_VAR_NAME= "resource_name"; //$NON-NLS-1$
	public static final String RESOURCE_ENC_VAR_NAME= "resource_enc"; //$NON-NLS-1$
	
	public static final String CONTAINER_LOC_VAR_NAME= "container_loc"; //$NON-NLS-1$
	public static final String CONTAINER_PATH_VAR_NAME= "container_path"; //$NON-NLS-1$
	public static final String CONTAINER_NAME_VAR_NAME= "container_name"; //$NON-NLS-1$
	public static final String CONTAINER_ENC_VAR_NAME= "container_enc"; //$NON-NLS-1$
	
	public static final String PROJECT_LOC_VAR_NAME= "project_loc"; //$NON-NLS-1$
	public static final String PROJECT_PATH_VAR_NAME= "project_path"; //$NON-NLS-1$
	public static final String PROJECT_NAME_VAR_NAME= "project_name"; //$NON-NLS-1$
	public static final String PROJECT_ENC_VAR_NAME= "project_enc"; //$NON-NLS-1$
	
	public static final String WORKSPACE_LOC= "workspace_loc"; //$NON-NLS-1$
	
	public static final String FILE_NAME_BASE_VAR_NAME= "file_name_base"; //$NON-NLS-1$
	public static final String FILE_NAME_EXT_VAR_NAME= "file_name_ext"; //$NON-NLS-1$
	
	
	public static final ImList<String> SINGLE_RESOURCE_VAR_NAMES= ImCollections.newList(
			RESOURCE_LOC_VAR_NAME, RESOURCE_PATH_VAR_NAME, RESOURCE_NAME_VAR_NAME, RESOURCE_ENC_VAR_NAME,
			CONTAINER_LOC_VAR_NAME, CONTAINER_PATH_VAR_NAME, CONTAINER_NAME_VAR_NAME, CONTAINER_ENC_VAR_NAME,
			PROJECT_LOC_VAR_NAME, PROJECT_PATH_VAR_NAME, PROJECT_NAME_VAR_NAME, PROJECT_ENC_VAR_NAME,
			FILE_NAME_BASE_VAR_NAME, FILE_NAME_EXT_VAR_NAME );
	
	private static volatile ImList<IDynamicVariable> singleResourceVariables;
	
	public static final ImList<IDynamicVariable> getSingleResourceVariables() {
		if (singleResourceVariables == null) {
			synchronized (SINGLE_RESOURCE_VAR_NAMES) {
				if (singleResourceVariables == null) {
					singleResourceVariables= createSingleResourceVars();
				}
			}
		}
		return singleResourceVariables;
	}
	
	private static final ImList<IDynamicVariable> createSingleResourceVars() {
		final ImList<String> names= ResourceVariables.SINGLE_RESOURCE_VAR_NAMES;
		final IDynamicVariable[] variables= new IDynamicVariable[names.size()];
		final IStringVariableManager manager= VariablesPlugin.getDefault().getStringVariableManager();
		for (int i= 0; i < variables.length; i++) {
			final String name= names.get(i);
			final IDynamicVariable globalVariable= manager.getDynamicVariable(name);
			if (globalVariable != null) {
				variables[i]= globalVariable;
			}
			else {
				variables[i]= new DynamicVariable.ResolverVariable(name, null, true,
						new ResourceVariableResolver() );
			}
		}
		return ImCollections.newList(variables);
	}
	
	public static final ImList<IDynamicVariable> createSingleResourceVarDefs(final String selectedResourceTerm) {
		final ImList<IDynamicVariable> globalVariables= getSingleResourceVariables();
		final IDynamicVariable[] variables= new IDynamicVariable[globalVariables.size()];
		
		final Matcher selResMatcher= Pattern.compile("selected resource", Pattern.LITERAL).matcher(""); //$NON-NLS-1$ 
		final String selResReplacement= Matcher.quoteReplacement(selectedResourceTerm);
		
		for (int i= 0; i < variables.length; i++) {
			final IDynamicVariable globalVariable= globalVariables.get(i);
			String description= globalVariable.getDescription();
			if (description != null) {
				description= selResMatcher.reset(description).replaceAll(selResReplacement);
			}
			variables[i]= new DynamicVariable(globalVariable.getName(), description,
					globalVariable.supportsArgument() );
		}
		return ImCollections.newList(variables);
	}
	
	
	private ResourceVariables() {}
	
}
