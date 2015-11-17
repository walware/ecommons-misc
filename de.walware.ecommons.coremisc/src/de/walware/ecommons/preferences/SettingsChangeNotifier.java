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

package de.walware.ecommons.preferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;

import de.walware.ecommons.IDisposable;
import de.walware.ecommons.preferences.internal.Messages;


public class SettingsChangeNotifier implements ISchedulingRule, IDisposable {
	
	
	public static interface ChangeListener {
		
		/**
		 * Is called inside a job. So, a bit longer tasks are allowed directly,
		 * but not UI tasks.
		 * 
		 * @param groupIds set of ids of changed preference groups
		 */
		public void settingsChanged(Set<String> groupIds);
		
	}
	
	public static interface ManageListener {
		
		public void beforeSettingsChangeNotification(Set<String> groupIds);
		
		public void afterSettingsChangeNotification(Set<String> groupIds);
		
	}
	
	
	private class NotifyJob extends Job {
		
		private final String fSource;
		private final Set<String> fChangedGroupIds= new HashSet<>();
		
		public NotifyJob(final String source) {
			super(Messages.SettingsChangeNotifier_Job_title);
			setPriority(Job.SHORT);
			setRule(SettingsChangeNotifier.this);
			fSource = source;
		}
		
		public void addGroups(final String[] groupIds) {
			fChangedGroupIds.addAll(Arrays.asList(groupIds));
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			Object[] managers;
			Object[] listeners;
			synchronized (SettingsChangeNotifier.this) {
				managers = fManagers.getListeners();
				listeners = fListeners.getListeners();
				fPendingJobs.remove(fSource);
			}
			monitor.beginTask(Messages.SettingsChangeNotifier_Task_name, managers.length*5+listeners.length*5);
			for (final Object obj : managers) {
				((ManageListener) obj).beforeSettingsChangeNotification(fChangedGroupIds);
				monitor.worked(3);
			}
			for (final Object obj : listeners) {
				((ChangeListener) obj).settingsChanged(fChangedGroupIds);
				monitor.worked(5);
			}
			for (final Object obj : managers) {
				((ManageListener) obj).afterSettingsChangeNotification(fChangedGroupIds);
				monitor.worked(2);
			}
			return Status.OK_STATUS;
		}
	}
	
	private final ListenerList fManagers = new ListenerList();
	private final ListenerList fListeners = new ListenerList();
	private final Map<String, NotifyJob> fPendingJobs= new HashMap<>();
	
	private volatile boolean fIsDisposed;
	
	
	public SettingsChangeNotifier() {
		fIsDisposed = false;
	}
	
	
	public Job getNotifyJob(String source, final String[] groupIds) {
		if (fIsDisposed) {
			return null;
		}
		if (source == null) {
			source = "direct"; //$NON-NLS-1$
		}
		synchronized (SettingsChangeNotifier.this) {
			NotifyJob job = fPendingJobs.get(source);
			if (job != null) {
				job.addGroups(groupIds);
				return null;
			}
			job = new NotifyJob(source);
			fPendingJobs.put(source, job);
			job.addGroups(groupIds);
			return job;
		}
	}
	
	@Override
	public boolean contains(final ISchedulingRule rule) {
		return (rule == this);
	}
	@Override
	public boolean isConflicting(final ISchedulingRule rule) {
		return (rule == this);
	}
	
	public void addChangeListener(final ChangeListener listener) {
		if (!fIsDisposed) {
			fListeners.add(listener);
		}
	}
	
	public void removeChangeListener(final ChangeListener listener) {
		if (!fIsDisposed) {
			fListeners.remove(listener);
		}
	}
	
	public void addManageListener(final ManageListener listener) {
		if (!fIsDisposed) {
			fManagers.add(listener);
		}
	}
	
	public void removeManageListener(final ManageListener listener) {
		if (!fIsDisposed) {
			fManagers.remove(listener);
		}
	}
	
	@Override
	public void dispose() {
		fIsDisposed = true;
		fManagers.clear();
		fListeners.clear();
	}
	
}
