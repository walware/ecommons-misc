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
 * A position updater that never deletes a position.
 * 
 * If the region containing the position is deleted, the position is moved to the beginning/end
 * (falling together) of the change.  If the region containing the position is replaced, the
 * position is placed at the same location inside the replacement text, but always inside the
 * replacement text.
 * 
 * @since de.walware.ecommons.text 1.0
 */
public class NonDeletingPositionUpdater implements IPositionUpdater {
	
	
	/** The position category. */
	private final String category;
	
	
	/**
	 * Creates a new updater for the given <code>category</code>.
	 *
	 * @param category the new category.
	 */
	public NonDeletingPositionUpdater(final String category) {
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
				
				if (offset > eventOldEndOffset) {
					// position comes way after change - shift
					position.setOffset(offset + eventNewLength - eventLength);
				}
				else if (endOffset < eventOffset) {
					// position comes way before change - leave alone
				}
				else if (offset <= eventOffset && endOffset >= eventOldEndOffset) {
					// event completely internal to the position - adjust length
					position.setLength(position.getLength() + eventNewLength - eventLength);
				}
				else if (offset < eventOffset) {
					// event extends over end of position - include change at end
					position.setLength(eventOffset + eventNewLength - offset);
				}
				else if (endOffset > eventOldEndOffset) {
					// event extends from before position into it - include change at begin
					position.setOffset(eventOffset);
					position.setLength(endOffset - eventOldEndOffset + eventNewLength);
				}
				else {
					// event comprises the position - keep it at the same
					// position, but always inside the replacement text
					final int eventNewEndOffset= eventOffset + eventNewLength;
					final int newOffset= Math.min(offset, eventNewEndOffset);
					final int newEndOffset= Math.min(endOffset, eventNewEndOffset);
					position.setOffset(newOffset);
					position.setLength(newEndOffset - newOffset);
				}
			}
		}
		catch (final BadPositionCategoryException e) {}
	}
	
}
