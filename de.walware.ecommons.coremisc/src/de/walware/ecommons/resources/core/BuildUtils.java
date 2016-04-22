/*=============================================================================#
 # Copyright (c) 2005-2016 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation in JDT
 #     Stephan Wahlbrink - adaptations to StatET
 #=============================================================================*/

package de.walware.ecommons.resources.core;

import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.io.internal.Messages;


public class BuildUtils {
	
	
	private static final class BuildJob extends Job {
		
		
		private final IWorkspace workspace;
		
		private final IProject project;
		
		
		private BuildJob(final String name, final IProject project) {
			super(name);
			
			this.workspace= ResourcesPlugin.getWorkspace();
			this.project= project;
			
			setPriority(Job.BUILD);
			setRule(this.project.getWorkspace().getRuleFactory().buildRule());
		}
		
		
		@Override
		public boolean belongsTo(final Object family) {
			return (family == ResourcesPlugin.FAMILY_MANUAL_BUILD);
		}
		
		public byte isCoveredBy(final BuildJob other) {
			if (Objects.equals(this.project, other.project)) {
				return 1;
			}
			if (other.project == null) {
				return 2;
			}
			return 0;
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final SubMonitor m= SubMonitor.convert(monitor);
			try {
				synchronized (getClass()) {
					if (m.isCanceled()) {
						throw new OperationCanceledException();
					}
					final Job[] buildJobs= Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_BUILD);
					for (final Job job : buildJobs) {
						if (job != this && job instanceof BuildJob) {
							final BuildJob buildJob= (BuildJob) job;
							switch (buildJob.isCoveredBy(this)) {
							case 1:
							case 2:
								buildJob.cancel();
								continue;
							default:
								continue;
							}
						}
					}
				}
				
				if (this.project != null) {
					m.beginTask(NLS.bind(Messages.CoreUtility_Build_ProjectTask_name, this.project.getName()), 2); 
					this.project.build(IncrementalProjectBuilder.FULL_BUILD, m.newChild(1));
					this.workspace.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, m.newChild(1));
				}
				else {
					m.beginTask(Messages.CoreUtility_Build_AllTask_name, 1); 
					this.workspace.build(IncrementalProjectBuilder.FULL_BUILD, m.newChild(1));
				}
			}
			catch (final CoreException e) {
				return e.getStatus();
			}
			catch (final OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
			finally {
				m.done();
			}
			return Status.OK_STATUS;
		}
		
	}
	
	private static final class CleanJob extends Job {
		
		
		private final IWorkspace workspace;
		
		private final IProject project;
		
		private boolean autoBuild;
		
		
		private CleanJob(final String name, final IProject project, final boolean autoBuild) {
			super(name);
			
			this.workspace= ResourcesPlugin.getWorkspace();
			this.project= project;
			
			this.autoBuild= autoBuild;
			
			setPriority(Job.LONG);
			setRule(this.workspace.getRuleFactory().buildRule());
		}
		
		
		@Override
		public boolean belongsTo(final Object family) {
			return (family == ResourcesPlugin.FAMILY_MANUAL_BUILD);
		}
		
		public byte isCoveredBy(final CleanJob other) {
			if (Objects.equals(this.project, other.project)) {
				return 1;
			}
			if (other.project == null) {
				return 2;
			}
			return 0;
		}
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			final SubMonitor m= SubMonitor.convert(monitor);
			try {
				synchronized (getClass()) {
					if (m.isCanceled()) {
						throw new OperationCanceledException();
					}
					final Job[] buildJobs= Job.getJobManager().find(ResourcesPlugin.FAMILY_MANUAL_BUILD);
					for (final Job job : buildJobs) {
						if (job != this && job instanceof CleanJob) {
							final CleanJob buildJob= (CleanJob) job;
							switch (buildJob.isCoveredBy(this)) {
							case 1:
								this.autoBuild|= buildJob.autoBuild;
								buildJob.cancel();
								continue;
							case 2:
								if (!this.autoBuild || buildJob.autoBuild) {
									continue;
								}
								buildJob.cancel();
								continue;
							default:
								continue;
							}
						}
					}
				}
				
				if (this.project != null) {
					m.beginTask(NLS.bind(Messages.CoreUtility_Clean_ProjectTask_name, this.project.getName()), 1); 
					this.project.build(IncrementalProjectBuilder.CLEAN_BUILD, m.newChild(1));
				}
				else {
					m.beginTask(Messages.CoreUtility_Clean_AllTask_name, 1); 
					this.workspace.build(IncrementalProjectBuilder.CLEAN_BUILD, m.newChild(1));
				}
				
				if (this.autoBuild) {
					final IWorkspaceDescription desc= this.workspace.getDescription();
					if (!desc.isAutoBuilding()) {
						getBuildJob(this.project).schedule();
					}
				}
			}
			catch (final CoreException e) {
				return e.getStatus();
			}
			catch (final OperationCanceledException e) {
				return Status.CANCEL_STATUS;
			}
			finally {
				m.done();
			}
			return Status.OK_STATUS;
		}
		
	}
	
	
	/**
	 * Returns a build job
	 * @param project The project to build or <code>null</code> to build the workspace.
	 */
	public static Job getBuildJob(final IProject project) {
		final Job buildJob= new BuildJob(Messages.CoreUtility_Build_Job_title, project);
		buildJob.setUser(true);
		return buildJob;
	}
	
	/**
	 * Returns a clean job
	 * @param project The project to build or <code>null</code> to build the workspace.
	 */
	public static Job getCleanJob(final IProject project, final boolean autoBuild) {
		final Job buildJob= new CleanJob(Messages.CoreUtility_Clean_Job_title, project, autoBuild);
		buildJob.setUser(true);
		return buildJob;
	}
	
	/**
	 * Starts a build in the background.
	 * @param project The project to build or <code>null</code> to build the workspace.
	 */
	public static void startBuildInBackground(final IProject project) {
		getBuildJob(project).schedule();
	}
	
	/**
	 * Sets whether building automatically is enabled in the workspace or not and returns 
	 * the old value.
	 * 
	 * @param state <code>true</code> if automatically building is enabled, 
	 *     <code>false</code> otherwise
	 * @return the old state
	 * @throws CoreException thrown if the operation failed
	 */
	public static boolean setAutoBuilding(final boolean state) throws CoreException {
		final IWorkspace workspace= ResourcesPlugin.getWorkspace();
		final IWorkspaceDescription desc= workspace.getDescription();
		final boolean isAutoBuilding= desc.isAutoBuilding();
		if (isAutoBuilding != state) {
			desc.setAutoBuilding(state);
			workspace.setDescription(desc);
		}
		return isAutoBuilding;
	}
	
	
	private BuildUtils() {}
	
}
