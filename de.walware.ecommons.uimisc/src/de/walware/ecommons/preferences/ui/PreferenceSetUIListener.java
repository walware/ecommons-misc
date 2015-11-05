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

package de.walware.ecommons.preferences.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import de.walware.jcommons.collections.ImSet;

import de.walware.ecommons.preferences.core.IPreferenceAccess;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.IPreferenceSetService.IChangeEvent;
import de.walware.ecommons.ui.util.UIAccess;


public abstract class PreferenceSetUIListener implements IPreferenceSetService.IChangeListener, Listener {
	
	
	public static PreferenceSetUIListener subscribe(
			final IPreferenceSetService.IChangeListener listener,
			final IPreferenceAccess prefAccess, final ImSet<String> qualifiers,
			final Control control) {
		final PreferenceSetUIListener uiListener= new PreferenceSetUIListener(prefAccess, control) {
			@Override
			protected void handlePreferenceChanged(IChangeEvent event) {
				listener.preferenceChanged(event);
			}
		};
		uiListener.subscribe(qualifiers);
		return uiListener;
	}
	
	
	private final IPreferenceAccess prefAccess;
	
	private final Control control;
	
	
	public PreferenceSetUIListener(final IPreferenceAccess prefAccess, final Control control) {
		this.prefAccess= prefAccess;
		this.control= control;
		
		this.control.addListener(SWT.Dispose, this);
		
	}
	
	
	public void subscribe(final ImSet<String> qualifiers) {
		this.prefAccess.addPreferenceSetListener(this, qualifiers);
	}
	
	public void unsubscribe() {
		this.prefAccess.removePreferenceSetListener(this);
	}
	
	@Override
	public void handleEvent(final Event event) {
		if (event.type == SWT.Dispose) {
			this.prefAccess.removePreferenceSetListener(this);
		}
	}
	
	@Override
	public void preferenceChanged(final IChangeEvent event) {
		UIAccess.getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				if (UIAccess.isOkToUse(control)) {
					handlePreferenceChanged(event);
				}
			}
		});
	}
	
	protected abstract void handlePreferenceChanged(final IChangeEvent event);
	
}
