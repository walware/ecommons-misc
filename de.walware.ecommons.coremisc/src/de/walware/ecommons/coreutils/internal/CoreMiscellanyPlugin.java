/*=============================================================================#
 # Copyright (c) 2007-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.coreutils.internal;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.ECommons.IAppEnvironment;
import de.walware.ecommons.IDisposable;
import de.walware.ecommons.preferences.SettingsChangeNotifier;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.internal.core.PreferenceSetService;


/**
 * The activator class controls the plug-in life cycle
 */
public final class CoreMiscellanyPlugin extends Plugin implements IAppEnvironment {
	
	
	public static final String PLUGIN_ID = "de.walware.ecommons.coremisc"; //$NON-NLS-1$
	
	
	/** The shared instance. */
	private static CoreMiscellanyPlugin gPlugin;
	
	/**
	 * Returns the shared plug-in instance
	 * 
	 * @return the shared instance
	 */
	public static CoreMiscellanyPlugin getDefault() {
		return gPlugin;
	}
	
	
	private boolean fStarted;
	
	private final List<IDisposable> fDisposables= new ArrayList<>();
	
	private PreferenceSetService preferenceSetService;
	
	private SettingsChangeNotifier fSettingsNotifier;
	
	
	/**
	 * The default constructor
	 */
	public CoreMiscellanyPlugin() {
		gPlugin = this;
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start(final BundleContext context) throws Exception {
		ECommons.init(PLUGIN_ID, this);
		super.start(context);
		
		synchronized (this) {
			fStarted = true;
			
			fSettingsNotifier = new SettingsChangeNotifier();
			addStoppingListener(fSettingsNotifier);
			
			this.preferenceSetService= new PreferenceSetService();
			addStoppingListener(this.preferenceSetService);
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void stop(final BundleContext context) throws Exception {
		try {
			synchronized (this) {
				fStarted = false;
			}
			
			try {
				for (final IDisposable listener : fDisposables) {
					listener.dispose();
				}
			}
			finally {
				fDisposables.clear();
			}
			
		}
		finally {
			gPlugin = null;
			super.stop(context);
		}
	}
	
	
	@Override
	public void log(final IStatus status) {
		getLog().log(status);
	}
	
	@Override
	public void addStoppingListener(final IDisposable listener) {
		if (listener == null) {
			throw new NullPointerException();
		}
		synchronized (this) {
			if (!fStarted) {
				throw new IllegalStateException("Plug-in is not started.");
			}
			fDisposables.add(listener);
		}
	}
	
	@Override
	public void removeStoppingListener(final IDisposable listener) {
		fDisposables.remove(listener);
	}
	
	public SettingsChangeNotifier getSettingsChangeNotifier() {
		return fSettingsNotifier;
	}
	
	public IPreferenceSetService getPreferenceSetService() {
		return this.preferenceSetService;
	}
	
}
