package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPartitioningException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentRewriteSession;
import org.eclipse.jface.text.DocumentRewriteSessionType;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.Position;


/**
 * @since de.walware.ecommons.text 1.1
 */
public abstract class AbstractSynchronizableDocument extends AbstractDocument
		implements ISynchronizable {
	
	
	private final Object lockObject;
	
	
	/**
	 * The default constructor does not perform any configuration but leaves it to the clients who
	 * must first initialize the implementation plug-ins and then call <code>completeInitialization</code>.
	 * Results in the construction of an empty document.
	 */
	public AbstractSynchronizableDocument() {
		super();
		
		this.lockObject= this;
	}
	
	/**
	 * The default constructor does not perform any configuration but leaves it to the clients who
	 * must first initialize the implementation plug-ins and then call <code>completeInitialization</code>.
	 * Results in the construction of an empty document.
	 */
	public AbstractSynchronizableDocument(final Object lockObject) {
		super();
		if (lockObject == null) {
			throw new NullPointerException("lockObject"); //$NON-NLS-1$
		}
		
		this.lockObject= lockObject;
	}
	
	
	
	@Override
	public final Object getLockObject() {
		return this.lockObject;
	}
	
	@Override
	public final void setLockObject(final Object lockObject) {
		throw new IllegalStateException("lock object already set"); //$NON-NLS-1$
	}
	
	
	@Override
	public String get() {
		synchronized (getLockObject()) {
			return super.get();
		}
	}
	
	@Override
	public String get(final int offset, final int length) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.get(offset, length);
		}
	}
	
	@Override
	public char getChar(final int offset) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getChar(offset);
		}
	}
	
	@Override
	public long getModificationStamp() {
		synchronized (getLockObject()) {
			return super.getModificationStamp();
		}
	}
	
	@Override
	public void replace(final int offset, final int length, final String text) throws BadLocationException {
		synchronized (getLockObject()) {
			super.replace(offset, length, text);
		}
	}
	
	@Override
	public void replace(final int offset, final int length, final String text, final long modificationStamp) throws BadLocationException {
		synchronized (getLockObject()) {
			super.replace(offset, length, text, modificationStamp);
		}
	}
	
	@Override
	public void set(final String text) {
		synchronized (getLockObject()) {
			super.set(text);
		}
	}
	
	@Override
	public void set(final String text, final long modificationStamp) {
		synchronized (getLockObject()) {
			super.set(text, modificationStamp);
		}
	}
	
	@Deprecated
	@Override
	public void startSequentialRewrite(final boolean normalized) {
		synchronized (getLockObject()) {
			super.startSequentialRewrite(normalized);
		}
	}
	
	@Deprecated
	@Override
	public void stopSequentialRewrite() {
		synchronized (getLockObject()) {
			super.stopSequentialRewrite();
		}
	}
	
	@Override
	public DocumentRewriteSession startRewriteSession(final DocumentRewriteSessionType sessionType) {
		synchronized (getLockObject()) {
			return super.startRewriteSession(sessionType);
		}
	}
	
	@Override
	public void stopRewriteSession(final DocumentRewriteSession session) {
		synchronized (getLockObject()) {
			super.stopRewriteSession(session);
		}
	}
	
	
	@Override
	public void addPositionCategory(final String category) {
		synchronized (getLockObject()) {
			super.addPositionCategory(category);
		}
	}
	
	@Override
	public void removePositionCategory(final String category) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.removePositionCategory(category);
		}
	}
	
	@Override
	public void addPosition(final String category, final Position position) throws BadLocationException, BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.addPosition(category, position);
		}
	}
	
	@Override
	public void removePosition(final String category, final Position position) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			super.removePosition(category, position);
		}
	}
	
	@Override
	public Position[] getPositions(final String category) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			return super.getPositions(category);
		}
	}
	
	@Override
	public Position[] getPositions(final String category, final int offset, final int length, final boolean canStartBefore, final boolean canEndAfter) throws BadPositionCategoryException {
		synchronized (getLockObject()) {
			return super.getPositions(category, offset, length, canStartBefore, canEndAfter);
		}
	}
	
	
	@Override
	public String[] getPartitionings() {
		synchronized (getLockObject()) {
			return super.getPartitionings();
		}
	}
	
	@Override
	public String getContentType(final String partitioning, final int offset, final boolean preferOpenPartitions)
			throws BadLocationException, BadPartitioningException {
		synchronized (getLockObject()) {
			return super.getContentType(partitioning, offset, preferOpenPartitions);
		}
	}
	
	@Override
	public ITypedRegion[] computePartitioning(final String partitioning, final int offset, final int length, final boolean includeZeroLengthPartitions) throws BadLocationException, BadPartitioningException {
		synchronized (getLockObject()) {
			return super.computePartitioning(partitioning, offset, length, includeZeroLengthPartitions);
		}
	}
	
	
	@Override
	public void setInitialLineDelimiter(final String lineDelimiter) {
		synchronized (getLockObject()) {
			super.setInitialLineDelimiter(lineDelimiter);
		}
	}
	
	@Override
	public String getDefaultLineDelimiter() {
		synchronized (getLockObject()) {
			return super.getDefaultLineDelimiter();
		}
	}
	
	@Override
	public String getLineDelimiter(final int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineDelimiter(line);
		}
	}
	
	
	@Override
	public int getNumberOfLines() {
		synchronized (getLockObject()) {
			return super.getNumberOfLines();
		}
	}
	
	@Override
	public int getNumberOfLines(final int offset, final int length) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getNumberOfLines(offset, length);
		}
	}
	
	@Override
	public int getLineOfOffset(final int pos) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineOfOffset(pos);
		}
	}
	
	@Override
	public int getLineOffset(final int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineOffset(line);
		}
	}
	
	@Override
	public int getLineLength(final int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineLength(line);
		}
	}
	
	@Override
	public IRegion getLineInformation(final int line) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineInformation(line);
		}
	}
	
	@Override
	public IRegion getLineInformationOfOffset(final int offset) throws BadLocationException {
		synchronized (getLockObject()) {
			return super.getLineInformationOfOffset(offset);
		}
	}
	
	
}
