/*=============================================================================#
 # Copyright (c) 2012-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.components;

import org.eclipse.swt.widgets.Control;


public interface IIntValueWidget {
	
	
	Control getControl();
	
	void addValueListener(IIntValueListener listener);
	void removeValueListener(IIntValueListener listener);
	
	int getValue(int idx);
	
	void setValue(int idx, int value);
	
}
