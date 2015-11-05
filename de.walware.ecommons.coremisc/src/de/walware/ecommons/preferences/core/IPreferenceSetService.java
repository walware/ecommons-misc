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

package de.walware.ecommons.preferences.core;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImSet;


public interface IPreferenceSetService {
	
	
	interface IChangeEvent {
		
		
		boolean contains(String qualifier);
		
		boolean contains(String qualifier, String key);
		boolean contains(Preference<?> pref);
		
		ImList<String> getKeys(String qualifier);
		
	}
	
	interface IChangeListener {
		
		
		void preferenceChanged(IChangeEvent event);
		
	}
	
	
	boolean pause(String sourceId);
	void resume(String sourceId);
	Job createResumeJob(String sourceId);
	
	void addChangeListener(IChangeListener listener,
			ImList<IScopeContext> contexts, ImSet<String> qualifiers);
	void removeChangeListener(IChangeListener listener);
	
}
