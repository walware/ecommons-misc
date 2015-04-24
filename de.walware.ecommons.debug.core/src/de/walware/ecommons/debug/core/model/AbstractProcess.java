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

package de.walware.ecommons.debug.core.model;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.model.IDebugTarget;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;

import de.walware.ecommons.debug.core.ECommonsDebugCore;


public abstract class AbstractProcess extends PlatformObject implements IProcess {
	
	
	private final ILaunch launch;
	
	private final String name;
	
	private volatile Map<String, String> attributes;
	
	private int exitValue;
	
	
	public AbstractProcess(final ILaunch launch, final String label) {
		this.launch= launch;
		this.name= label;
	}
	
	
	@Override
	public ILaunch getLaunch() {
		return this.launch;
	}
	
	@Override
	public String getLabel() {
		return this.name;
	}
	
	@Override
	public IStreamsProxy getStreamsProxy() {
		return null;
	}
	
	
	protected void fireEvent(final DebugEvent event) {
		final DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(new DebugEvent[] { event });
		}
	}
	
	protected void fireEvents(final DebugEvent[] events) {
		final DebugPlugin manager= DebugPlugin.getDefault();
		if (manager != null) {
			manager.fireDebugEventSet(events);
		}
	}
	
	
	protected final Map<String, String> getAttributes(final boolean create) {
		Map<String, String> map= this.attributes;
		if (create && map == null) {
			synchronized (this) {
				if (this.attributes == null) {
					this.attributes= new HashMap<>();
				}
				map= this.attributes;
			}
		}
		return map;
	}
	
	protected final DebugEvent doSet(final Map<String, String> map,
			final String key, final String value) {
		final String oldValue= map.put(key, value);
		if (oldValue == value
				|| (oldValue != null && oldValue.equals(value)) ) {
			return null;
		}
		final DebugEvent event= new DebugEvent(this, DebugEvent.CHANGE);
		event.setData(new String[] { key, oldValue, value });
		return event;
	}
	
	@Override
	public void setAttribute(final String key, final String value) {
		final Map<String, String> attributes= getAttributes(true);
		final DebugEvent event;
		synchronized (attributes) {
			event= doSet(attributes, key, value);
		}
		if (event != null) {
			fireEvent(event);
		}
	}
	
	@Override
	public String getAttribute(final String key) {
		final Map<String, String> attributes= getAttributes(false);
		if (attributes != null) {
			synchronized (attributes) {
				return attributes.get(key);
			}
		}
		return null;
	}
	
	
	protected void created() {
		getLaunch().addProcess(this);
		fireEvent(new DebugEvent(this, DebugEvent.CREATE));
	}
	
	protected void terminated() {
		fireEvent(new DebugEvent(this, DebugEvent.TERMINATE));
	}
	
	protected void doSetExitValue(final int value) {
		this.exitValue= value;
	}
	
	protected int doGetExitValue() {
		return this.exitValue;
	}
	
	@Override
	public int getExitValue() throws DebugException {
		if (!isTerminated()) {
			throw new DebugException(new Status(IStatus.ERROR, ECommonsDebugCore.PLUGIN_ID,
					DebugException.TARGET_REQUEST_FAILED,
					"Exit value is not available until process is terminated.", null)); 
		}
		return doGetExitValue();
	}
	
	
	@Override
	public Object getAdapter(final Class required) {
		if (required == IProcess.class) {
			return this;
		}
		if (required == ILaunch.class) {
			return getLaunch();
		}
		if (required == IDebugTarget.class) {
			final ILaunch launch = getLaunch();
			final IDebugTarget[] targets = launch.getDebugTargets();
			for (int i = 0; i < targets.length; i++) {
				if (equals(targets[i].getProcess())) {
					return targets[i];
				}
			}
			return null;
		}
		return super.getAdapter(required);
	}
	
}
