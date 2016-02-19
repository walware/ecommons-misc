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

package de.walware.ecommons.io;

import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.variables.IStringVariable;

import de.walware.ecommons.variables.core.IObservableVariable;

public class ObservableFileValidator extends FileValidator {
	
	
	private class ResourceObservableValue extends AbstractObservableValue {
		
		
		private IResource value;
		
		
		public ResourceObservableValue(final Realm realm) {
			super(realm);
		}
		
		
		@Override
		public Object getValueType() {
			return IResource.class;
		}
		
		@Override
		protected Object doGetValue() {
			return this.value;
		}
		
		@Override
		protected void doSetValue(final Object value) {
			setExplicit(value);
			checkExplicit();
		}
		
		void update(final IResource newValue) {
			if ((newValue != null) ? !newValue.equals(this.value) : null != this.value) {
				fireValueChange(Diffs.createValueDiff(this.value, this.value= newValue));
			}
		}
		
	}
	
	private class FileStoreObservableValue extends AbstractObservableValue {
		
		
		private IFileStore value;
		
		
		public FileStoreObservableValue(final Realm realm) {
			super(realm);
		}
		
		
		@Override
		public Object getValueType() {
			return IFileStore.class;
		}
		
		@Override
		protected Object doGetValue() {
			return this.value;
		}
		
		@Override
		protected void doSetValue(final Object value) {
			setExplicit(value);
			checkExplicit();
		}
		
		void update(final IFileStore newValue) {
			if ((newValue != null) ? !newValue.equals(this.value) : null != this.value) {
				fireValueChange(Diffs.createValueDiff(this.value, this.value= newValue));
			}
		}
		
	}
	
	
	private final Realm realm;
	
	private IChangeListener observableListener;
	
	private ResourceObservableValue resourceObservable;
	private FileStoreObservableValue fileStoreObservable;
	
	
	public ObservableFileValidator(final Realm realm) {
		this.realm= realm;
	}
	
	
	@Override
	void checkVariable(final IStringVariable variable) {
		if (variable instanceof IObservableVariable) {
			if (this.observableListener == null) {
				this.observableListener= new IChangeListener() {
					@Override
					public void handleChange(final ChangeEvent event) {
						updateVariableResolution();
					}
				};
			}
			((IObservableVariable) variable).addChangeListener(this.observableListener);
		}
	}
	
	@Override
	protected void setStatus(final IStatus status) {
		super.setStatus(status);
		
		if (this.resourceObservable != null) {
			this.resourceObservable.update(getWorkspaceResource());
		}
		if (this.fileStoreObservable != null) {
			this.fileStoreObservable.update(getFileStore());
		}
	}
	
	public IObservableValue getWorkspaceResourceObservable() {
		if (this.resourceObservable == null) {
			this.resourceObservable= new ResourceObservableValue(this.realm);
			this.resourceObservable.update(getWorkspaceResource());
		}
		return this.resourceObservable;
	}
	
	public IObservableValue getFileStoreObservable() {
		if (this.resourceObservable == null) {
			this.fileStoreObservable= new FileStoreObservableValue(this.realm);
			this.fileStoreObservable.update(getFileStore());
		}
		return this.fileStoreObservable;
	}
	
}
