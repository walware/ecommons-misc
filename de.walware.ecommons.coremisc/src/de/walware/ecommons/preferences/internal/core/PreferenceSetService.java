/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.internal.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.IScopeContext;

import de.walware.jcommons.collections.CopyOnWriteList;
import de.walware.jcommons.collections.ImCollection;
import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;
import de.walware.jcommons.collections.ImListBuilder;
import de.walware.jcommons.collections.ImSet;

import de.walware.ecommons.IDisposable;
import de.walware.ecommons.preferences.core.IPreferenceSetService;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.internal.Messages;


public final class PreferenceSetService implements IPreferenceSetService, ISchedulingRule, IDisposable {
	
	
	private static final int MS_NS= 1000 * 1000;
	
	private static final int MIN_WAIT_NS= 50 * MS_NS;
	private static final int AUTO_WAIT_NS= 200 * MS_NS;
	private static final int TOLERANCE_NS= MIN_WAIT_NS / 2;
	
	private static final String DISPOSE_SOURCE= "disposed"; //$NON-NLS-1$
	
	
	private static class ChangeListenerItem {
		
		
		private final IChangeListener listener;
		
		private final ImList<IScopeContext> contexts;
		
		private final ImSet<String> qualifiers;
		
		
		public ChangeListenerItem(final IChangeListener listener,
				final ImList<IScopeContext> contexts, final ImSet<String> qualifiers) {
			this.listener= listener;
			this.contexts= contexts;
			this.qualifiers= qualifiers;
		}
		
		
		public IChangeListener getListener() {
			return this.listener;
		}
		
		public ImList<IScopeContext> getContexts() {
			return this.contexts;
		}
		
		public ImSet<String> getQualifiers() {
			return this.qualifiers;
		}
		
	}
	
	private class ContextItem {
		
		private final IScopeContext context;
		
		private final ConcurrentHashMap<String, NodeItem> nodes= new ConcurrentHashMap<>();
		
		
		public ContextItem(final IScopeContext context) {
			this.context= context;
		}
		
		
		public IScopeContext getContext() {
			return this.context;
		}
		
		
		public void watch(final ImCollection<String> qualifiers) {
			for (final String qualifier : qualifiers) {
				if (!this.nodes.contains(qualifier)) {
					this.nodes.putIfAbsent(qualifier,
							new NodeItem(this.context, qualifier) );
				}
			}
		}
		
		public void check(final List<NodeItem> items) {
			for (final NodeItem nodeItem : this.nodes.values()) {
				if (nodeItem.check()) {
					items.add(nodeItem);
				}
			}
		}
		
	}
	
	private class NodeItem implements IPreferenceChangeListener {
		
		private final IScopeContext context;
		
		private final String qualifier;
		
		private final IEclipsePreferences node;
		
		private final List<String> newKeys= new ArrayList<>();
		private ImList<String> checkedKeys= ImCollections.emptyList();
		
		
		public NodeItem(final IScopeContext context, final String qualifier) {
			this.context= context;
			this.qualifier= qualifier;
			this.node= context.getNode(qualifier);
			this.node.addPreferenceChangeListener(this);
		}
		
		
		public IScopeContext getContext() {
			return this.context;
		}
		
		public String getQualifier() {
			return this.qualifier;
		}
		
		@Override
		public void preferenceChange(final PreferenceChangeEvent event) {
			onPreferenceChange();
			synchronized (this.newKeys) {
				this.newKeys.add(event.getKey());
			}
		}
		
		public boolean check() {
			synchronized (this.newKeys) {
				this.checkedKeys= ImCollections.toList(this.newKeys);
				if (this.checkedKeys.isEmpty()) {
					return false;
				}
				else {
					this.newKeys.clear();
					return true;
				}
			}
		}
		
		public ImList<String> getChangedKeys() {
			return this.checkedKeys;
		}
		
	}
	
	private class Event implements IChangeEvent {
		
		
		private final IdentityHashMap<String, ImList<String>> qualifierKeyMap= new IdentityHashMap<>(32);
		
		
		@Override
		public boolean contains(final String qualifier) {
			return this.qualifierKeyMap.containsKey(qualifier);
		}
		
		@Override
		public boolean contains(final String qualifier, String key) {
			final ImList<String> events= this.qualifierKeyMap.get(qualifier);
			if (events != null) {
				if (key.charAt(key.length() - 1) == '*') {
					key= key.substring(0, key.length() - 1);
					for (int i= 0; i < events.size(); i++) {
						if (events.get(i).startsWith(key)) {
							return true;
						}
					}
				}
				else {
					for (int i= 0; i < events.size(); i++) {
						if (events.get(i).equals(key)) {
							return true;
						}
					}
				}
			}
			return false;
		}
		
		@Override
		public boolean contains(final Preference<?> pref) {
			return contains(pref.getQualifier(), pref.getKey());
		}
		
		@Override
		public ImList<String> getKeys(final String qualifier) {
			return this.qualifierKeyMap.get(qualifier);
		}
		
		public void add(final String qualifier, final ImList<String> keys) {
			final ImList<String> present= this.qualifierKeyMap.put(qualifier, keys);
			if (present != null) {
				this.qualifierKeyMap.put(qualifier,
						ImCollections.concatList(present, keys) );
			}
		}
		
		public boolean isEmpty() {
			return this.qualifierKeyMap.isEmpty();
		}
		
		public void reset() {
			this.qualifierKeyMap.clear();
		}
		
	}
	
	private class NotifyJob extends Job {
		
		
		private final String pauseKey;
		
		
		public NotifyJob(final String pauseKey) {
			super(Messages.SettingsChangeNotifier_Job_title);
			
			this.pauseKey= pauseKey;
			
			setSystem(true);
			setPriority(SHORT);
			setRule(PreferenceSetService.this);
		}
		
		
		@Override
		protected IStatus run(final IProgressMonitor monitor) {
			if (this.pauseKey != null) {
				synchronized (PreferenceSetService.this) {
					if (!removePause(this.pauseKey)) {
						return Status.OK_STATUS;
					}
				}
			}
			
			process(monitor);
			
			return Status.OK_STATUS;
		}
		
	}
	
	
	
	private final Set<String> pauseIds= new HashSet<>();
	
	private CopyOnWriteList<ChangeListenerItem> listeners= new CopyOnWriteList<>();
	
	private final Map<IScopeContext, ContextItem> contexts= new HashMap<>();
	private ImList<ContextItem> contextList;
	
	private final Job defaultJob= new NotifyJob(null);
	private volatile long minStamp;
	private volatile long scheduledStamp;
	
	private final Map<IScopeContext, ImList<NodeItem>> processMap= new HashMap<>();
	private final ImListBuilder<?> processListBuilder= new ImListBuilder<>();
	private final Event processEvent= new Event();
	
	
	public PreferenceSetService() {
	}
	
	
	@Override
	public synchronized void dispose() {
		this.pauseIds.add(DISPOSE_SOURCE);
	}
	
	@Override
	public synchronized boolean pause(final String sourceId) {
		if (this.pauseIds.isEmpty()) {
			this.defaultJob.cancel();
		}
		return this.pauseIds.add(sourceId);
	}
	
	private boolean removePause(final String sourceId) {
		return (this.pauseIds.remove(sourceId)
				&& this.pauseIds.isEmpty() );
	}
	
	@Override
	public synchronized void resume(final String sourceId) {
		if (removePause(sourceId)) {
			this.scheduledStamp= System.nanoTime() + MIN_WAIT_NS;
			this.defaultJob.schedule((MIN_WAIT_NS + TOLERANCE_NS) / MS_NS);
		}
	}
	
	private synchronized void onPreferenceChange() {
		if (!this.pauseIds.isEmpty()) {
			final long time= System.nanoTime() + MIN_WAIT_NS;
			if (time > this.minStamp) {
				this.minStamp= time;
			}
			return;
		}
		else {
			final long time= System.nanoTime() + AUTO_WAIT_NS;
			if (time > this.minStamp) {
				this.minStamp= time;
			}
			if (time > this.scheduledStamp + TOLERANCE_NS) {
				this.defaultJob.cancel();
				this.scheduledStamp= time;
				this.defaultJob.schedule((AUTO_WAIT_NS + TOLERANCE_NS) / MS_NS);
			}
		}
	}
	
	private synchronized boolean isOkToRun(final long time) {
		if (!this.pauseIds.isEmpty()) {
			return false;
		}
		else if (time < this.minStamp) {
			this.scheduledStamp= this.minStamp;
			this.defaultJob.schedule((this.minStamp - System.nanoTime() + TOLERANCE_NS) / MS_NS);
			return false;
		}
		else {
			return true;
		}
	}
	
	@Override
	public Job createResumeJob(final String key) {
		return new NotifyJob(key);
	}
	
	@Override
	public boolean contains(final ISchedulingRule rule) {
		return (rule == this);
	}
	@Override
	public boolean isConflicting(final ISchedulingRule rule) {
		return (rule == this);
	}
	
	
	private ContextItem getContextItem(final IScopeContext context) {
		synchronized (this.contexts) {
			ContextItem contextItem= this.contexts.get(context);
			if (contextItem == null) {
				contextItem= new ContextItem(context);
				this.contexts.put(context, contextItem);
				this.contextList= null;
			}
			return contextItem;
		}
	}
	
	private ImList<ContextItem> getContextItems() {
		synchronized (this.contexts) {
			if (this.contextList == null) {
				this.contextList= ImCollections.toList(this.contexts.values());
			}
			return this.contextList;
		}
	}
	
	public void watch(final IScopeContext context, final ImSet<String> qualifiers) {
		getContextItem(context).watch(qualifiers);
	}
	
	
	@Override
	public void addChangeListener(final IChangeListener listener,
			final ImList<IScopeContext> contexts, final ImSet<String> qualifiers) {
		if (listener == null) {
			throw new NullPointerException("listener"); //$NON-NLS-1$
		}
		if (contexts == null) {
			throw new NullPointerException("contexts"); //$NON-NLS-1$
		}
		if (qualifiers == null) {
			throw new NullPointerException("qualifiers"); //$NON-NLS-1$
		}
		
		LISTENER: synchronized (this.listeners) {
			final ChangeListenerItem item= new ChangeListenerItem(listener, contexts, qualifiers);
			final ImList<ChangeListenerItem> l= this.listeners.toList();
			for (int i= 0; i < l.size(); i++) {
				final ChangeListenerItem iItem= l.get(i);
				if (iItem.getListener() == listener) {
					this.listeners.set(i, item);
					break LISTENER;
				}
			}
			this.listeners.add(item);
		}
		
		for (final IScopeContext context : contexts) {
			if (context instanceof DefaultScope) {
				continue;
			}
			watch(context, qualifiers);
		}
	}
	
	@Override
	public void removeChangeListener(final IChangeListener listener) {
		LISTENER: synchronized (this.listeners) {
			final ImList<ChangeListenerItem> l= this.listeners.toList();
			for (int i= 0; i < l.size(); i++) {
				if (l.get(i).getListener() == listener) {
					this.listeners.remove(i);
					break LISTENER;
				}
			}
		}
	}
	
	
	private void process(final IProgressMonitor monitor) {
		final Map<IScopeContext, ImList<NodeItem>> map= this.processMap;
		
		final long time= System.nanoTime();
		if (!isOkToRun(time)) {
			return;
		}
		
		{	final List<ContextItem> contextItems= getContextItems();
			@SuppressWarnings("unchecked")
			final ImListBuilder<NodeItem> items= (ImListBuilder<NodeItem>) this.processListBuilder;
			items.clear();
			final boolean merge= !map.isEmpty();
			for (final ContextItem contextItem : contextItems) {
				contextItem.check(items);
				if (!items.isEmpty()) {
					if (merge) {
						final ImList<NodeItem> oldItems= map.get(contextItem.getContext());
						if (oldItems != null) {
							items.addAll(oldItems);
						}
					}
					map.put(contextItem.getContext(), items.build());
					items.clear();
					
					if (!isOkToRun(time)) {
						return;
					}
				}
			}
		}
		
		try {
			final Event event= this.processEvent;
			final ImList<ChangeListenerItem> listeners= this.listeners.toList();
			for (final ChangeListenerItem listener : listeners) {
				for (final IScopeContext context : listener.getContexts()) {
					final ImList<NodeItem> list= map.get(context);
					if (list != null) {
						for (final NodeItem nodeItem : list) {
							if (listener.getQualifiers().contains(nodeItem.getQualifier())) {
								event.add(nodeItem.getQualifier(), nodeItem.getChangedKeys());
							}
						}
					}
				}
				
				if (!event.isEmpty()) {
					listener.getListener().preferenceChanged(event);
					
					event.reset();
				}
			}
		}
		finally {
			map.clear();
		}
	}
	
}
