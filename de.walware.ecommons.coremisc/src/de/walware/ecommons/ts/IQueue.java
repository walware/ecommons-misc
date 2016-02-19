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

package de.walware.ecommons.ts;

import org.eclipse.core.runtime.IStatus;


/**
 * Queue of a {@link ITool} to schedule {@link IToolRunnable}s.
 */
public interface IQueue {
	
	/**
	 * Schedules the runnable in the regular queue.
	 * <p>
	 * The method returns directly after the queue operation is done. The runnable is run when it is
	 * its turn.</p>
	 * <p>
	 * A runnable can be added multiple times to schedule it multiple times.</p>
	 * 
	 * @param runnable the runnable to add to the queue
	 * @return status of the queue operation.
	 */
	IStatus add(IToolRunnable runnable);
	
	/**
	 * Removes the runnable from the queue.
	 * <p>
	 * If the runnable exists multiple times in the queue, the first runnable (in order they would 
	 * be run) will be removed. If the runnable is not in the queue, nothing is done.</p>
	 * <p>
	 * If a runnable is removed, the operation trigger a event of type {@link IToolRunnable#REMOVING_FROM}.
	 * </p>
	 * 
	 * @param runnable the runnable to remove from the queue
	 */
	void remove(IToolRunnable runnable);
	
	/**
	 * Returns if the instance supports real hot scheduling of runnables.
	 * 
	 * @return <code>true</code> if it is supported, otherwise false
	 * @see #addHot(IToolRunnable)
	 */
	boolean isHotSupported();
	
	/**
	 * Schedules the runnable in the hot queue.
	 * <p>
	 * Runnables in the hot queue are run as soon as possible, also before and within runnables 
	 * of the regular queue.  It should be used only  for short task and not changing global data.  
	 * A typical use case are minor updates in the GUI.</p>
	 * <p>
	 * A runnable can be added multiple times to schedule it multiple times.</p>
	 * 
	 * @param runnable the runnable to add to the queue
	 * @return status of the queue operation.
	 */
	IStatus addHot(IToolRunnable runnable);
	
	/**
	 * Removes the runnable from the queue.
	 * <p>
	 * If the runnable exists multiple times in the queue, the first runnable (in order they would 
	 * be run) will be removed. If the runnable is not in the queue, nothing is done.</p>
	 * <p>
	 * If a runnable is removed, the operation trigger a event of type {@link IToolRunnable#REMOVING_FROM}.
	 * </p>
	 * 
	 * @param runnable the runnable to remove from the queue
	 */
	void removeHot(IToolRunnable runnable);
	
	
}
