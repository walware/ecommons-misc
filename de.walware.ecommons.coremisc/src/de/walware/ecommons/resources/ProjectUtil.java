/*******************************************************************************
 * Copyright (c) 2006-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.resources;

import org.eclipse.core.resources.IProjectDescription;


public class ProjectUtil {
	
	
	public static IProjectDescription appendNature(final IProjectDescription description, final String id) {
		final String[] prevNatures = description.getNatureIds();
		for (int i = 0; i < prevNatures.length; i++) {
			if (prevNatures[i].equals(id)) {
				return description;
			}
		}
		final String[] newNatures = new String[prevNatures.length + 1];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);
		newNatures[prevNatures.length] = id;
		description.setNatureIds(newNatures);
		return description;
	}
	
	public static IProjectDescription removeNature(final IProjectDescription description, final String id) {
		final String[] prevNatures = description.getNatureIds();
		for (int i = 0; i < prevNatures.length; i++) {
			if (prevNatures[i].equals(id)) {
				final String[] newNatures = new String[prevNatures.length - 1];
				System.arraycopy(prevNatures, 0, newNatures, 0, i);
				System.arraycopy(prevNatures, i+1, newNatures, i, prevNatures.length-i-1);
				description.setNatureIds(newNatures);
				break;
			}
		}
		return description;
	}
	
	
	private ProjectUtil() {
	}
	
}
