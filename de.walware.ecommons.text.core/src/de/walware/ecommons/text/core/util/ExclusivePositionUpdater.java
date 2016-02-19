/*=============================================================================#
 # Copyright (c) 2005-2016 IBM Corporation and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 #
 # Contributors:
 #     IBM Corporation - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.text.core.util;

import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.Position;


/**
 * A position updater that takes any changes not completely in
 * <code>(p.offset, p.offset + p.length)</code> of a {@link Position position} <code>p</code> to not
 * belong to the position.
 * 
 * @since de.walware.ecommons.text 1.1
 */
public class ExclusivePositionUpdater implements IPositionUpdater {
	
	
	/** The position category. */
	private final String category;
	
	
	/**
	 * Creates a new updater for the given <code>category</code>.
	 *
	 * @param category the new category.
	 */
	public ExclusivePositionUpdater(final String category) {
		if (category == null) {
			throw new NullPointerException("category"); //$NON-NLS-1$
		}
		this.category= category;
	}
	
	
	/**
	 * Returns the position category.
	 *
	 * @return the position category
	 */
	public final String getCategory() {
		return this.category;
	}
	
	
	@Override
	public void update(final DocumentEvent event) {
		final int eventOffset= event.getOffset();
		final int eventLength= event.getLength();
		final int eventOldEndOffset= eventOffset + eventLength;
		final int eventNewLength= (event.getText() == null) ? 0 : event.getText().length();
		
		try {
			final Position[] positions= event.getDocument().getPositions(this.category);
			
			for (int i= 0; i != positions.length; i++) {
				final Position position= positions[i];
				
				if (position.isDeleted()) {
					continue;
				}
				
				final int offset= position.getOffset();
				final int endOffset= offset + position.getLength();
				
				if (offset >= eventOldEndOffset) {
					// position comes after change - shift
					position.setOffset(offset + eventNewLength - eventLength);
				}
				else if (endOffset <= eventOffset) {
					// position comes way before change - leave alone
				}
				else if (offset <= eventOffset && endOffset >= eventOldEndOffset) {
					// event completely internal to the position - adjust length
					position.setLength(position.getLength() + eventNewLength - eventLength);
				}
				else if (offset < eventOffset) {
					// event extends over end of position - cut change at end
					position.setLength(eventOffset - offset);
				}
				else if (endOffset > eventOldEndOffset) {
					// event extends from before position into it - cut change at begin
					final int newOffset= eventOffset + eventNewLength;
					position.setOffset(newOffset);
					position.setLength(endOffset - eventOldEndOffset);
				}
				else {
					// event consumes the position - delete it
					position.delete();
				}
			}
		}
		catch (final BadPositionCategoryException e) {}
	}
	
}
