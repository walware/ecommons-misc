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

package de.walware.ecommons.databinding.core;

import org.eclipse.core.databinding.util.Policy;
import org.eclipse.core.runtime.IStatus;


public class DataStatus implements IStatus {
	
	
	public static final int UPDATEABLE_ERROR= 0x03;
	
	
	public static final DataStatus OK_STATUS= new DataStatus(OK, "ok"); //$NON-NLS-1$
	
	
	public static int getInfoSeverity(final IStatus status) {
		if (status instanceof DataStatus) {
			return ((DataStatus) status).getInfoSeverity();
		}
		final IStatus[] children= status.getChildren();
		if (children.length > 0) {
			int maxSeverity= -1;
			for (int i= 0; i < children.length; i++) {
				final int severity= getInfoSeverity(children[i]);
				if (severity > maxSeverity) {
					maxSeverity= severity;
				}
			}
			return maxSeverity;
		}
		return status.getSeverity();
	}
	
	
	private static final IStatus[] NO_CHILDREN= new IStatus[0];
	
	
	private final int severity;
	
	private final String message;
	
	
	public DataStatus(final int severity, final String message) {
		this.severity= severity;
		this.message= (message != null) ? message : ""; //$NON-NLS-1$
	}
	
	
	@Override
	public final int getSeverity() {
		switch (this.severity) {
		case UPDATEABLE_ERROR:
			return WARNING;
		default:
			return this.severity;
		}
	}
	
	public final int getInfoSeverity() {
		switch (this.severity) {
		case UPDATEABLE_ERROR:
			return ERROR;
		default:
			return this.severity;
		}
	}
	
	@Override
	public final boolean isOK() {
		return (this.severity == 0);
	}
	
	@Override
	public boolean matches(final int severityMask) {
		return ((getSeverity() & severityMask) != 0);
	}
	
	
	@Override
	public String getPlugin() {
		return Policy.JFACE_DATABINDING;
	}
	
	@Override
	public int getCode() {
		return 0;
	}
	
	@Override
	public String getMessage() {
		return this.message;
	}
	
	@Override
	public Throwable getException() {
		return null;
	}
	
	
	@Override
	public boolean isMultiStatus() {
		return false;
	}
	
	@Override
	public IStatus[] getChildren() {
		return NO_CHILDREN;
	}
	
	
	@Override
	public int hashCode() {
		return this.severity * 31 + this.message.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DataStatus)) {
			return false;
		}
		final DataStatus other= (DataStatus) obj;
		return (this.severity == other.severity && this.message.equals(other.message));
	}
	
}
