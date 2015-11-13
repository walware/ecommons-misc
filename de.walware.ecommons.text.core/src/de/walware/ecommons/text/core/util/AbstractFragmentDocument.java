/*=============================================================================#
 # Copyright (c) 2005-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.util;

import java.util.ArrayList;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jface.text.IDocumentPartitionerExtension2;
import org.eclipse.jface.text.IDocumentPartitioningListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TypedRegion;

import de.walware.ecommons.text.core.IFragmentDocument;
import de.walware.ecommons.text.core.ITextRegion;
import de.walware.ecommons.text.core.TextRegion;
import de.walware.ecommons.text.internal.core.ECommonsTextCorePlugin;


/**
 * @since de.walware.ecommons.text 1.1
 */
public abstract class AbstractFragmentDocument extends AbstractSynchronizableDocument
		implements IFragmentDocument {
	
	
	private static final byte NO_CHANGE= 0;
	private static final byte PREFIX_CHANGE= 1;
	private static final byte FRAGMENT_CHANGE= 2;
	
	
	private class PartitionerMapper implements IDocumentPartitioner {
		
		private final IDocumentPartitioner masterPartitioner;
		
		public PartitionerMapper(final IDocumentPartitioner partitioner) {
			this.masterPartitioner= partitioner;
		}
		
		@Override
		public void connect(final IDocument document) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void disconnect() {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public void documentAboutToBeChanged(final DocumentEvent event) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public boolean documentChanged(final DocumentEvent event) {
			throw new UnsupportedOperationException();
		}
		
		@Override
		public ITypedRegion[] computePartitioning(final int offset, final int length) {
			return remap(this.masterPartitioner.computePartitioning(
					AbstractFragmentDocument.this.offsetInMaster + offset, length));
		}
		
		@Override
		public String getContentType(final int offset) {
			return this.masterPartitioner.getContentType(AbstractFragmentDocument.this.offsetInMaster + offset);
		}
		
		@Override
		public String[] getLegalContentTypes() {
			return this.masterPartitioner.getLegalContentTypes();
		}
		
		@Override
		public ITypedRegion getPartition(final int offset) {
			return remap(this.masterPartitioner.getPartition(AbstractFragmentDocument.this.offsetInMaster + offset));
		}
	}
	
	private class PartitionerMapper2 extends PartitionerMapper implements IDocumentPartitionerExtension2 {
		
		private final IDocumentPartitionerExtension2 masterPartitioner2;
		
		public PartitionerMapper2(final IDocumentPartitioner partitioner) {
			super(partitioner);
			this.masterPartitioner2= (IDocumentPartitionerExtension2) partitioner;
		}
		
		@Override
		public ITypedRegion[] computePartitioning(final int offset, final int length, final boolean includeZeroLengthPartitions) {
			return this.masterPartitioner2.computePartitioning(AbstractFragmentDocument.this.offsetInMaster + offset, length, includeZeroLengthPartitions);
		}
		
		@Override
		public String getContentType(final int offset, final boolean preferOpenPartitions) {
			return this.masterPartitioner2.getContentType(AbstractFragmentDocument.this.offsetInMaster + offset, preferOpenPartitions);
		}
		
		@Override
		public String[] getManagingPositionCategories() {
			return this.masterPartitioner2.getManagingPositionCategories();
		}
		
		@Override
		public ITypedRegion getPartition(final int offset, final boolean preferOpenPartitions) {
			return this.masterPartitioner2.getPartition(AbstractFragmentDocument.this.offsetInMaster + offset, preferOpenPartitions);
		}
		
	}
	
	
	private final AbstractDocument master;
	
	private int offsetInMaster= 0;
	
	private byte change= NO_CHANGE;
	
	
	/**
	 * The default constructor does not perform any configuration but leaves it to the clients who
	 * must first initialize the implementation plug-ins and then call <code>completeInitialization</code>.
	 * Results in the construction of an empty document.
	 */
	public AbstractFragmentDocument() {
		super();
		
		this.master= createMasterDocument();
		
		this.master.addDocumentListener(new IDocumentListener() {
			@Override
			public void documentAboutToBeChanged(final DocumentEvent event) {
				switch (AbstractFragmentDocument.this.change) {
				case PREFIX_CHANGE:
					AbstractFragmentDocument.this.offsetInMaster+= - event.fLength + event.fText.length();
					return;
				case FRAGMENT_CHANGE:
					return;
				default:
					throw new UnsupportedOperationException();
				}
			}
			@Override
			public void documentChanged(final DocumentEvent event) {
			}
		});
		this.master.addDocumentPartitioningListener(new IDocumentPartitioningListener() {
			@Override
			@SuppressWarnings("deprecation")
			public void documentPartitioningChanged(final IDocument document) {
				fireDocumentPartitioningChanged(new Region(0, getLength()));
			}
		});
	}
	
	
	@Override
	protected void completeInitialization() {
		super.completeInitialization();
		
		addDocumentListener(new IDocumentListener() {
			@Override
			public void documentAboutToBeChanged(final DocumentEvent event) {
			}
			@Override
			public void documentChanged(final DocumentEvent event) {
				if (AbstractFragmentDocument.this.change != NO_CHANGE) {
					throw new IllegalStateException(String.valueOf(AbstractFragmentDocument.this.change));
				}
				
				AbstractFragmentDocument.this.change= FRAGMENT_CHANGE;
				try {
					replaceInMaster(AbstractFragmentDocument.this.offsetInMaster + event.fOffset,
							event.fLength, event.fText );
				}
				catch (final BadLocationException e) {
					ECommonsTextCorePlugin.log(new Status(IStatus.ERROR, ECommonsTextCorePlugin.PLUGIN_ID,
							"Failed to edit master document.", e ));
				}
				finally {
					AbstractFragmentDocument.this.change= NO_CHANGE;
				}
			}
		});
	}
	
	
	protected abstract AbstractDocument createMasterDocument();
	
	@Override
	public AbstractDocument getMasterDocument() {
		return this.master;
	}
	
	@Override
	public int getOffsetInMasterDocument() {
		return this.offsetInMaster;
	}
	
	@Override
	public ITextRegion getRegionInMasterDocument() {
		synchronized (getLockObject()) {
			return new TextRegion(this.offsetInMaster, this.offsetInMaster + getLength());
		}
	}
	
	protected void replaceInMaster(final int offset, final int length, final String text)
			throws BadLocationException {
		this.master.replace(offset, length, text);
	}
	
	
	protected void setPrefix(final String text) {
		if (text == null) {
			throw new NullPointerException();
		}
		synchronized (getLockObject()) {
			if (this.change != NO_CHANGE) {
				throw new IllegalStateException(String.valueOf(this.change));
			}
			
			this.change= PREFIX_CHANGE;
			try {
				replaceInMaster(0, this.offsetInMaster, text);
				this.offsetInMaster= text.length();
			}
			catch (final BadLocationException e) {
				ECommonsTextCorePlugin.log(new Status(IStatus.ERROR, ECommonsTextCorePlugin.PLUGIN_ID,
						"Failed to edit master document.", e ));
			}
			finally {
				this.change= 0;
			}
		}
	}
	
	
	@Override
	public void setInitialLineDelimiter(final String lineDelimiter) {
		this.master.setInitialLineDelimiter(lineDelimiter);
		super.setInitialLineDelimiter(lineDelimiter);
	}
	
	
	@Override
	public void setDocumentPartitioner(final IDocumentPartitioner partitioner) {
		this.master.setDocumentPartitioner(partitioner);
	}
	
	@Override
	public void setDocumentPartitioner(final String partitioning, final IDocumentPartitioner partitioner) {
		this.master.setDocumentPartitioner(partitioning, partitioner);
	}
	
	@Override
	public String[] getPartitionings() {
		return this.master.getPartitionings();
	}
	
	@Override
	public IDocumentPartitioner getDocumentPartitioner() {
		return getDocumentPartitioner(DEFAULT_PARTITIONING);
	}
	
	@Override
	public IDocumentPartitioner getDocumentPartitioner(final String partitioning) {
		final IDocumentPartitioner masterPartitioner= this.master.getDocumentPartitioner(partitioning);
		if (masterPartitioner instanceof IDocumentPartitionerExtension2) {
			return new PartitionerMapper2(masterPartitioner);
		}
		else {
			return new PartitionerMapper(masterPartitioner);
		}
	}
	
	@Override
	public String[] getLegalContentTypes() {
		return this.master.getLegalContentTypes();
	}
	
	@Override
	public String[] getLegalContentTypes(final String partitioning) throws BadPartitioningException {
		return this.master.getLegalContentTypes(partitioning);
	}
	
	@Override
	public ITypedRegion[] computePartitioning(final int offset, final int length) throws BadLocationException {
		return remap(this.master.computePartitioning(this.offsetInMaster + offset, length));
	}
	
	@Override
	public ITypedRegion[] computePartitioning(final String partitioning, final int offset, final int length,
			final boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
		return remap(this.master.computePartitioning(partitioning, this.offsetInMaster + offset, length, includeZeroLengthPartitions));
	}
	
	@Override
	public ITypedRegion getPartition(final int offset) throws BadLocationException {
		return remap(this.master.getPartition(this.offsetInMaster + offset));
	}
	
	@Override
	public ITypedRegion getPartition(final String partitioning, final int offset,
			final boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		return remap(this.master.getPartition(partitioning, this.offsetInMaster + offset, preferOpenPartitions));
	}
	
	@Override
	public String getContentType(final int offset) throws BadLocationException {
		return this.master.getContentType(this.offsetInMaster + offset);
	}
	
	@Override
	public String getContentType(final String partitioning, final int offset,
			final boolean preferOpenPartitions) throws BadLocationException, BadPartitioningException {
		return this.master.getContentType(partitioning, this.offsetInMaster + offset, preferOpenPartitions);
	}
	
	
	private ITypedRegion[] remap(final ITypedRegion[] masterRegions) {
		final ArrayList<IRegion> regions= new ArrayList<>(masterRegions.length);
		for (final ITypedRegion masterRegion : masterRegions) {
			final ITypedRegion region= remap(masterRegion);
			if (region != null) {
				regions.add(region);
			}
		}
		return regions.toArray(new ITypedRegion[regions.size()]);
	}
	
	private ITypedRegion remap(final ITypedRegion masterRegion) {
		int offset= masterRegion.getOffset() - this.offsetInMaster;
		int length= masterRegion.getLength();
		if (offset + length >= 0) {
			if (offset < 0) {
				length= length + offset;
				offset= 0;
			}
//			System.out.println("partitiong mapping: ("+masterRegion.getOffset()+", "+masterRegion.getLength()+") -["+fOffsetInMaster+"]-> ("+offset+", "+length+")");
			return new TypedRegion(offset, length, masterRegion.getType());
		}
		
		{	final BadPartitioningException e= new BadPartitioningException("Failed to map partition from master to fragment document.");
			e.fillInStackTrace();
			ECommonsTextCorePlugin.log(new Status(IStatus.ERROR, ECommonsTextCorePlugin.PLUGIN_ID,
					"Failed to compute partition.", e ));
			return null;
		}
	}
	
}
