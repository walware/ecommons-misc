/*******************************************************************************
 * Copyright (c) 2006-2012 WalWare/StatET-Project (www.walware.de/goto/statet).
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.ts;


/**
 * Central handler for a tool.
 * It provides access to its properties and the queue.
 */
public interface ITool {
	
	/**
	 * Constant for {@link #getLabel(int)} for the default label
	 */
	int DEFAULT_LABEL =                                     0x00000000;
	
	/**
	 * Constant for {@link #getLabel(int)} for a longer label with more information
	 */
	int LONG_LABEL =                                        0x00000001;
	
	
	/**
	 * Returns the main type of the tool.
	 * 
	 * @return id of the main tool type
	 */
	String getMainType();
	
	/**
	 * Returns if the tool provides the given feature set.
	 * 
	 * @param featureSetId id of the feature set
	 * @return <code>true</code> if the features are supported, otherwise <code>false</code>
	 */
	boolean isProvidingFeatureSet(String featureSetId);
	
	/**
	 * Returns the runnable queue of the tool
	 * 
	 * @return the queue
	 */
	IQueue getQueue();
	
	/**
	 * Returns whether the tool is terminated.
	 * 
	 * @return <code>true</code> if terminated or disconnected, otherwise <code>false</code>
	 */
	boolean isTerminated();
	
	/**
	 * Returns a label for the tool.
	 * 
	 * Global config options: {@link #DEFAULT_LABEL}, {@link #LONG_LABEL}
	 * 
	 * @param config allows to configure the information to include in the label
	 * @return the label
	 */
	String getLabel(int config);
	
}
