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

package de.walware.ecommons.resources;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IProjectDescription;


public class ProjectUtil {
	
	
	private static int indexOf(final String[] ids, final String id) {
		for (int idx= 0; idx < ids.length; idx++) {
			if (ids[idx].equals(id)) {
				return idx;
			}
		}
		return -1;
	}
	
	private static int indexOf(final ICommand[] commands, final String id) {
		for (int idx= 0; idx < commands.length; idx++) {
			if (commands[idx].getBuilderName().equals(id)) {
				return idx;
			}
		}
		return -1;
	}
	
	/**
	 * Adds the specified project nature to the project description.
	 * 
	 * @param description
	 * @param natureId id of the nature to add
	 * @return <code>true</code> if the project description is changed by this method
	 */
	public static boolean addNature(final IProjectDescription description,
			final String natureId) {
		final String[] prevNatures= description.getNatureIds();
		final int idx= indexOf(prevNatures, natureId);
		
		if (idx == -1) {
			final String[] newNatures= new String[prevNatures.length + 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
			newNatures[prevNatures.length]= natureId;
			description.setNatureIds(newNatures);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Removes the specified project nature to the project description.
	 * 
	 * @param description
	 * @param natureId id of the nature to remove
	 * @return <code>true</code> if the project description is changed by this method
	 */
	public static boolean removeNature(final IProjectDescription description,
			final String natureId) {
		final String[] prevNatures= description.getNatureIds();
		final int idx= indexOf(prevNatures, natureId);
		
		if (idx >= 0) {
			final String[] newNatures= new String[prevNatures.length - 1];
			System.arraycopy(prevNatures, 0, newNatures, 0, idx);
			System.arraycopy(prevNatures, idx + 1, newNatures, idx, prevNatures.length - idx - 1);
			description.setNatureIds(newNatures);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds the specified project builder to the project description.
	 * 
	 * @param description
	 * @param builderId the id of the builder to add
	 * @return <code>true</code> if the project description is changed by this method
	 */
	public static boolean addBuilder(final IProjectDescription description,
			final String builderId) {
		final ICommand[] prevCommands= description.getBuildSpec();
		final int idx= indexOf(prevCommands, builderId);
		
		if (idx == -1) {
			final ICommand newCommand= description.newCommand();
			newCommand.setBuilderName(builderId);
			final ICommand[] newCommands= new ICommand[prevCommands.length+1];
			System.arraycopy(prevCommands, 0, newCommands, 0, prevCommands.length);
			newCommands[prevCommands.length]= newCommand;
			description.setBuildSpec(newCommands);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Adds the specified project builder to the project description.
	 * 
	 * @param description
	 * @param builderId the id of the builder to add
	 * @return <code>true</code> if the project description is changed by this method
	 */
	public static boolean removeBuilder(final IProjectDescription description,
			final String builderId) {
		final ICommand[] prevCommands= description.getBuildSpec();
		final int idx= indexOf(prevCommands, builderId);
		
		if (idx >= 0) {
			final ICommand[] newCommands= new ICommand[prevCommands.length - 1];
			System.arraycopy(prevCommands, 0, newCommands, 0, idx);
			System.arraycopy(prevCommands, idx + 1, newCommands, idx, prevCommands.length - idx - 1);
			description.setBuildSpec(newCommands);
			return true;
		}
		
		return false;
	}
	
	
	private ProjectUtil() {
	}
	
}
