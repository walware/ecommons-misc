/*=============================================================================#
 # Copyright (c) 2006-2016 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     IBM Corporation - initial API and implementation of WritableValue
 #     Stephan Wahlbrink - initial WritableEqualityValue
 #=============================================================================*/

package de.walware.ecommons.databinding.core.observable;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;


public class WritableEqualityValue extends AbstractObservableValue {
	
	
	private final Object valueType;
	
	private Object value= null;
	
	
	/**
	 * Constructs a new instance with the default realm, a <code>null</code>
	 * value type, and a <code>null</code> value.
	 */
	public WritableEqualityValue() {
		this(null, null);
	}
	
	/**
	 * Constructs a new instance with the default realm.
	 *
	 * @param initialValue
	 *            can be <code>null</code>
	 * @param valueType
	 *            can be <code>null</code>
	 */
	public WritableEqualityValue(final Object initialValue, final Class valueType) {
		this(Realm.getDefault(), initialValue, valueType);
	}
	
	/**
	 * Constructs a new instance with the provided <code>realm</code>, a
	 * <code>null</code> value type, and a <code>null</code> initial value.
	 *
	 * @param realm
	 */
	public WritableEqualityValue(final Realm realm) {
		this(realm, null, null);
	}
	
	/**
	 * Constructs a new instance.
	 *
	 * @param realm
	 * @param initialValue
	 *            can be <code>null</code>
	 * @param valueType
	 *            can be <code>null</code>
	 */
	public WritableEqualityValue(final Realm realm, final Object initialValue, final Class valueType) {
		super(realm);
		this.valueType= valueType;
		this.value= initialValue;
	}
	
	
	@Override
	public Object getValueType() {
		return this.valueType;
	}
	
	@Override
	public Object doGetValue() {
		return this.value;
	}
	
	@Override
	public void doSetValue(final Object value) {
		final Object oldValue= doGetValue();
		if (value != oldValue && (value == null || !value.equals(oldValue))) {
			fireValueChange(Diffs.createValueDiff(this.value, this.value= value));
		}
	}
	
}
