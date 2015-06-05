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

package de.walware.ecommons.text.core.treepartitioner;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

import de.walware.ecommons.text.core.treepartitioner.ITreePartitionNodeScan.BreakException;


public class TestPartitionNodeScanner implements ITreePartitionNodeScanner {
	
	
	@Override
	public int getRestartOffset(final ITreePartitionNode node, final IDocument document, final int offset)
			throws BadLocationException {
		return 0;
	}
	
	@Override
	public AbstractPartitionNodeType getRootType() {
		return TestPartitionNodeType.DEFAULT_ROOT;
	}
	
	
	@Override
	public void execute(final ITreePartitionNodeScan scan) throws BreakException {
		try {
			final IDocument document= scan.getDocument();
			ITreePartitionNode node= scan.getBeginNode();
			int offset= scan.getBeginOffset();
			while (offset < scan.getEndOffset()) {
				switch (document.getChar(offset)) {
				case '{':
					node= scan.add(TestPartitionNodeType.T1, node, offset);
					break;
				case '[':
					node= scan.add(TestPartitionNodeType.T2, node, offset);
					break;
				case '(':
					node= scan.add(TestPartitionNodeType.T3, node, offset);
					break;
				case '}':
				case ']':
				case ')':
					scan.expand(node, offset + 1, true);
					node= node.getParent();
					break;
				default:
					break;
				}
				offset++;
			}
			if (TestPartitionNodeType.DEFAULT_ROOT != node.getType()) {
				throw new AssertionError(node.getType().toString());
			}
		}
		catch (final BadLocationException e) {
			throw new AssertionError(e);
		}
	}
	
}
