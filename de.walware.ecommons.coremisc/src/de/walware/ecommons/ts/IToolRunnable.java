/*******************************************************************************
 * Copyright (c) 2006-2011 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ts;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;


/**
 * Runnable for a {@link ITool}.
 */
public interface IToolRunnable {
	
	
	/**
	 * Total work of progress monitors.
	 * Value = {@value}
	 */
	int TOTAL_WORK = 10000;
	
	
	int MASK_EVENT_GROUP =                                         0x00000ff0;
	
	int ADDING_EVENT_GROUP =                                       0x00000110;
	int REMOVING_EVENT_GROUP =                                     0x00000120;
	int STARTING_EVENT_GROUP =                                     0x00000140;
	int FINISHING_EVENT_GROUP =                                    0x00000150;
	
	
	/**
	 * Adding runnable to queue by add
	 */
	int ADDING_TO =                                                ADDING_EVENT_GROUP;
	/**
	 * Adding runnable to queue by move
	 */
	int MOVING_TO =                                                ADDING_EVENT_GROUP | 0x1;
	
	/**
	 * Removing runnable from queue by explicit remove
	 */
	int REMOVING_FROM =                                            REMOVING_EVENT_GROUP;
	/**
	 * Removing runnable from queue by move
	 */
	int MOVING_FROM =                                              REMOVING_EVENT_GROUP | 0x1;
	/**
	 * Removing runnable from queue because the tool is disposed
	 */
	int BEING_ABANDONED =                                          REMOVING_EVENT_GROUP | 0x2;
	
	/**
	 * Starting to run the runnable (includes removing runnable from queue)
	 */
	int STARTING =                                                 STARTING_EVENT_GROUP;
	/**
	 * Finishing runnable normally
	 */
	int FINISHING_OK =                                             FINISHING_EVENT_GROUP | IStatus.OK;
	/**
	 * Finishing runnable after an error occurred
	 */
	int FINISHING_ERROR =                                          FINISHING_EVENT_GROUP | IStatus.ERROR;
	/**
	 * Finishing runnable after canceling was handled
	 */
	int FINISHING_CANCEL =                                         FINISHING_EVENT_GROUP | IStatus.CANCEL;
	
	
	/**
	 * Returns the id of the runnable type.
	 * 
	 * @return the id
	 */
	String getTypeId();
	
	/**
	 * Return a label for this runnable, used by the UI.
	 * 
	 * @return the label
	 */
	String getLabel();
	
	/**
	 * Checks if the runnable can be run in the given tool.
	 * 
	 * Implementations can for example check if all required features are supported using
	 * {@link ITool#isProvidingFeatureSet(String)}.
	 * 
	 * @param tool
	 * @return <code>true</code> if the tool is accepted, otherwise <code>false</code>
	 */
	boolean isRunnableIn(ITool tool);
	
	/**
	 * Is called when the state of the runnable is changing
	 * 
	 * Return value has only effect for the following events:
	 * {@link IQueue#ENTRIES_DELETE}, {@link IQueue#ENTRIES_MOVE_DELETE}
	 * 
	 * @param event the event id
	 * @param tool the related tool
	 * 
	 * @return <code>false</code> to vote against the operation, otherwise <code>true</code>
	 */
	boolean changed(int event, ITool tool);
	
	/**
	 * This method is called by the tool controller, when this instance is one's turn.
	 * <p>
	 * This method is running in the tool-thread and blocks the thread,
	 * until <code>run</code> is finished. So you have exclusive access to
	 * the tool inside this method.
	 * <p>
	 * Don't call this method on another place.
	 * <p>
	 * The monitor is already setup with main label of getLabel() and total
	 * work of {@link #TOTAL_WORK}.
	 * 
	 * @param adapter your interface to the tool
	 * @param monitor a progress monitor (you can check for cancel)
	 * @throws CoreException if an error occurred or the runnable was canceled
	 */
	void run(IToolService service, IProgressMonitor monitor) throws CoreException;
	
}
