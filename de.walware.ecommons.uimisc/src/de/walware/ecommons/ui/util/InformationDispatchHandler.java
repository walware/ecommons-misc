/*=============================================================================#
 # Copyright (c) 2009-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.ui.util;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.bindings.keys.SWTKeySupport;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.keys.IBindingService;

import de.walware.ecommons.ui.internal.Messages;


public class InformationDispatchHandler extends AbstractHandler {
	
	
	public static final String COMMAND_ID = "org.eclipse.ui.edit.text.showInformation"; //$NON-NLS-1$
	
	
	public static final int MODE_TOOLTIP = 1;
	public static final int MODE_PROPOSAL_INFO = 2;
	
	public static final int MODE_FOCUS = 0x10;
	
	
	public static final String getTooltipAffordanceString() {
		final IBindingService bindingService = (IBindingService) PlatformUI.getWorkbench().getAdapter(IBindingService.class);
		if (bindingService == null) {
			return null;
		}
		
		final String keySequence = bindingService.getBestActiveBindingFormattedFor(InformationDispatchHandler.COMMAND_ID);
		if (keySequence == null) {
			return ""; //$NON-NLS-1$
		}
		
		return NLS.bind(Messages.Hover_TooltipFocusAffordance_message, keySequence);
	}
	
	public static final String getAdditionalInfoAffordanceString() {
		final String keySequence = SWTKeySupport.getKeyFormatterForPlatform().format(SWT.TAB);
		
		return NLS.bind(Messages.Hover_ProposalInfoFocusAffordance_message, keySequence);
	}
	
	public static final String getAffordanceString(final int mode) {
		switch (mode) {
		case MODE_TOOLTIP:
			return getTooltipAffordanceString();
		case MODE_PROPOSAL_INFO:
			return getAdditionalInfoAffordanceString();
		default:
			return ""; //$NON-NLS-1$
		}
	}
	
	
	private final ColumnWidgetTokenOwner fTokenOwner;
	
	
	/**
	 * Creates a dispatch action.
	 */
	public InformationDispatchHandler(final ColumnWidgetTokenOwner owner) {
		fTokenOwner = owner;
	}
	
	
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		if (fTokenOwner != null) {
			if (fTokenOwner.moveFocusToWidgetToken()) {
				return null;
			}
		}
		
		return showInformation();
	}
	
	protected Object showInformation() {
		return null;
	}
	
}
