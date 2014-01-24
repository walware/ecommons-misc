/*=============================================================================#
 # Copyright (c) 2013-2014 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.models.core.util;

import java.lang.reflect.Array;


/**
 * Automatically creates partitions of large collections.
 * 
 * Supports <code>long</code> collections and avoids widows.
 */
public abstract class ElementPartitionFactory<E, T> {
	
	
	public static final int DEFAULT_PART_SIZE = 100;
	private static final int PART_LAST_ADD = 10;
	
	
	public final class PartitionHandle {
		
		private final long dim;
		
		private final long start;
		private final long length;
		
		
		private PartitionHandle(final long dim, final long start, final long length) {
			this.dim = dim;
			this.start = start;
			this.length = length;
		}
		
		
		public long getStart() {
			return this.start;
		}
		
		public long getLength() {
			return this.length;
		}
		
		public E[] getElements(final T value) {
			return (this.dim == 1) ?
					getChildren(value, this.start, (int) this.length) :
					createPartitions(value, this.dim, this.start, this.length);
		}
		
		
		@Override
		public int hashCode() {
			final long s = (this.start >>> 1) + 7;
			return (int) (this.dim ^ (this.dim >>> 32) ^ s ^ (s >>> 32));
		}
		
		@Override
		public boolean equals(final Object obj) {
			if (this == obj) {
				return true;
			}
			return (obj instanceof ElementPartitionFactory.PartitionHandle)
					&& equals((PartitionHandle) obj);
		}
		
		public boolean equals(final ElementPartitionFactory<?, ?>.PartitionHandle other) {
			return (this.dim == other.dim && this.start == other.start);
		}
		
	}
	
	
	private final int partSize;
	private final int partLastSize;
	
	private final Class<E> elementClass;
	
	
	public ElementPartitionFactory(final Class<E> elementClass, final int partSize) {
		this.elementClass = elementClass;
		this.partSize = partSize;
		this.partLastSize = partSize + PART_LAST_ADD;
	}
	
	
	public E[] getElements(final T value, final long length) {
		if (length <= this.partLastSize) {
			return getChildren(value, 0, (int) length);
		}
		long dim = 1;
		long l;
		do {
			if ((length % dim) > PART_LAST_ADD) {
				dim *= this.partSize;
				l = length / dim + 1;
			}
			else {
				dim *= this.partSize;
				l = length / dim;
			}
		} while (l > this.partLastSize);
		
		return createPartitions(value, dim, 0, length);
	}
	
	private E[] createPartitions(final T value, final long dim,
			final long start, final long length) {
		final long nextDim = dim / this.partSize;
		final E[] variables = (E[]) Array.newInstance(this.elementClass,
				((length / nextDim) % this.partSize > PART_LAST_ADD) ?
						(int) (length / dim + 1) : (int) (length / dim) );
		final int last = variables.length - 1;
		for (int i = 0; i < last; i++) {
			variables[i] = createPartition(value, new PartitionHandle(nextDim,
					start + i * dim, dim ));
		}
		variables[last] = createPartition(value, new PartitionHandle(nextDim,
				start + last * dim, length - last * dim ));
		
		return variables;
	}
	
	protected abstract E createPartition(final T value, final PartitionHandle partition);
	
	protected abstract E[] getChildren(T value, long start, int length);
	
}
