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

import org.eclipse.jface.internal.text.InformationControlReplacer;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IInformationControlExtension3;
import org.eclipse.jface.text.IInformationControlExtension5;
import org.eclipse.jface.text.IViewportListener;
import org.eclipse.jface.text.IWidgetTokenKeeper;
import org.eclipse.jface.text.IWidgetTokenKeeperExtension;
import org.eclipse.jface.text.IWidgetTokenOwner;
import org.eclipse.jface.util.Geometry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;


/**
 * Implements a sticky hover control, i.e. a control that replaces a hover
 * with an enriched and focusable control.
 * For ColumnViewer (instead of TextViewer).
 * <p>
 * The information control is made visible on request by calling
 * {@link #showInformationControl(Rectangle)}.
 * </p>
 * <p>
 * Clients usually instantiate and configure this class before using it. The configuration
 * must be consistent: This means the used {@link org.eclipse.jface.text.IInformationControlCreator}
 * must create an information control expecting information in the same format the configured
 * {@link org.eclipse.jface.text.information.IInformationProvider}s use to encode the information they provide.
 * </p>
 */
public class ColumnHoverStickyManager extends InformationControlReplacer implements IWidgetTokenKeeper, IWidgetTokenKeeperExtension {
	
	/**
	 * Priority of the info controls managed by this sticky hover manager.
	 * <p>
	 * Note: Only applicable when info control does not have focus.
	 * -5 as value has been chosen in order to be beaten by the hovers of TextViewerHoverManager.
	 * </p>
	 */
	private static final int WIDGET_PRIORITY= -5;
	
	
	/**
	 * Internal information control closer. Listens to several events issued by its subject control
	 * and closes the information control when necessary.
	 */
	class Closer implements IInformationControlCloser, ControlListener, MouseListener, IViewportListener, KeyListener, FocusListener, Listener {
		//TODO: Catch 'Esc' key in fInformationControlToClose: Don't dispose, just hideInformationControl().
		// This would allow to reuse the information control also when the user explicitly closes it.
		
		//TODO: if subject control is a Scrollable, should add selection listeners to both scroll bars
		// (and remove the ViewPortListener, which only listens to vertical scrolling)
		
		/** The subject control. */
		private Control fSubjectControl;
		/** Indicates whether this closer is active. */
		private boolean fIsActive= false;
		/** The display. */
		private Display fDisplay;
		
		
		@Override
		public void setSubjectControl(final Control control) {
			fSubjectControl= control;
		}
		
		@Override
		public void setInformationControl(final IInformationControl control) {
			// NOTE: we use getCurrentInformationControl2() from the outer class
		}
		
		@Override
		public void start(final Rectangle informationArea) {
			if (fIsActive) {
				return;
			}
			fIsActive= true;
			
			if (fSubjectControl != null && !fSubjectControl.isDisposed()) {
				fSubjectControl.addControlListener(this);
				fSubjectControl.addMouseListener(this);
				fSubjectControl.addKeyListener(this);
			}
			
			final IInformationControl fInformationControlToClose= getCurrentInformationControl2();
			if (fInformationControlToClose != null) {
				fInformationControlToClose.addFocusListener(this);
			}
			
			fDisplay= fSubjectControl.getDisplay();
			if (!fDisplay.isDisposed()) {
				fDisplay.addFilter(SWT.MouseMove, this);
				fDisplay.addFilter(SWT.FocusOut, this);
			}
		}
		
		@Override
		public void stop() {
			if (!fIsActive) {
				return;
			}
			fIsActive= false;
			
			if (fSubjectControl != null && !fSubjectControl.isDisposed()) {
				fSubjectControl.removeControlListener(this);
				fSubjectControl.removeMouseListener(this);
				fSubjectControl.removeKeyListener(this);
			}
			
			final IInformationControl fInformationControlToClose= getCurrentInformationControl2();
			if (fInformationControlToClose != null) {
				fInformationControlToClose.removeFocusListener(this);
			}
			
			if (fDisplay != null && !fDisplay.isDisposed()) {
				fDisplay.removeFilter(SWT.MouseMove, this);
				fDisplay.removeFilter(SWT.FocusOut, this);
			}
			fDisplay= null;
		}
		
		 @Override
		public void controlResized(final ControlEvent e) {
			 hideInformationControl();
		}
		 
		 @Override
		public void controlMoved(final ControlEvent e) {
			 hideInformationControl();
		}
		 
		 @Override
		public void mouseDown(final MouseEvent e) {
			 hideInformationControl();
		}
		
		@Override
		public void mouseUp(final MouseEvent e) {
		}
		
		@Override
		public void mouseDoubleClick(final MouseEvent e) {
			hideInformationControl();
		}
		
		@Override
		public void viewportChanged(final int topIndex) {
			hideInformationControl();
		}
		
		@Override
		public void keyPressed(final KeyEvent e) {
			hideInformationControl();
		}
		
		@Override
		public void keyReleased(final KeyEvent e) {
		}
		
		@Override
		public void focusGained(final FocusEvent e) {
		}
		
		@Override
		public void focusLost(final FocusEvent e) {
			if (DEBUG) {
				System.out.println("StickyHoverManager.Closer.focusLost(): " + e); //$NON-NLS-1$
			}
			final Display d= fSubjectControl.getDisplay();
			d.asyncExec(new Runnable() {
				// Without the asyncExec, mouse clicks to the workbench window are swallowed.
				@Override
				public void run() {
					hideInformationControl();
				}
			});
		}
		
		@Override
		public void handleEvent(final Event event) {
			if (event.type == SWT.MouseMove) {
				if (!(event.widget instanceof Control) || event.widget.isDisposed()) {
					return;
				}
				final IInformationControl infoControl= getCurrentInformationControl2();
				if (infoControl != null && !infoControl.isFocusControl() && infoControl instanceof IInformationControlExtension3) {
//					if (DEBUG) System.out.println("StickyHoverManager.Closer.handleEvent(): activeShell= " + fDisplay.getActiveShell()); //$NON-NLS-1$
					final IInformationControlExtension3 iControl3= (IInformationControlExtension3) infoControl;
					final Rectangle controlBounds= iControl3.getBounds();
					if (controlBounds != null) {
						final Point mouseLoc= event.display.map((Control) event.widget, null, event.x, event.y);
						final int margin= getKeepUpMargin();
						Geometry.expand(controlBounds, margin, margin, margin, margin);
						if (!controlBounds.contains(mouseLoc)) {
							hideInformationControl();
						}
					}
				}
				else {
					/*
					 * TODO: need better understanding of why/if this is needed.
					 * Looks like the same panic code we have in org.eclipse.jface.text.AbstractHoverInformationControlManager.Closer.handleMouseMove(Event)
					 */
					if (fDisplay != null && !fDisplay.isDisposed()) {
						fDisplay.removeFilter(SWT.MouseMove, this);
					}
				}
			}
			else if (event.type == SWT.FocusOut) {
				if (DEBUG) {
					System.out.println("StickyHoverManager.Closer.handleEvent(): focusOut: " + event); //$NON-NLS-1$
				}
				final IInformationControl iControl= getCurrentInformationControl2();
				if (iControl != null && ! iControl.isFocusControl()) {
					hideInformationControl();
				}
			}
		}
	}
	
	
	private final ColumnWidgetTokenOwner fTokenOwner;
	private IWidgetTokenKeeper fTokenKeeperToReplace;
	
	
	/**
	 * Creates a new sticky hover manager.
	 * 
	 * @param textViewer the text viewer
	 */
	public ColumnHoverStickyManager(final ColumnWidgetTokenOwner owner, final IWidgetTokenKeeper keeperToReplace) {
		super(new AbstractReusableInformationControlCreator() {
			@Override
			public IInformationControl doCreateInformationControl(final Shell shell) {
				return new DefaultInformationControl(shell, true);
			}
		});
		
		fTokenOwner = owner;
		fTokenKeeperToReplace = keeperToReplace;
		setCloser(new Closer());
		
		install(fTokenOwner.getControl());
	}
	
	@Override
	protected void showInformationControl(final Rectangle subjectArea) {
		if (fTokenOwner != null && fTokenOwner.requestWidgetToken(this, WIDGET_PRIORITY)) {
			super.showInformationControl(subjectArea);
		} else
			if (DEBUG) {
				System.out.println("cancelled StickyHoverManager.showInformationControl(..): did not get widget token (with prio)"); //$NON-NLS-1$
			}
	}
	
	@Override
	public void hideInformationControl() {
		try {
			super.hideInformationControl();
		} finally {
			if (fTokenOwner != null) {
				fTokenOwner.releaseWidgetToken(this);
			}
		}
	}
	
	@Override
	protected void handleInformationControlDisposed() {
		try {
			super.handleInformationControlDisposed();
		} finally {
			if (fTokenOwner != null) {
				fTokenOwner.releaseWidgetToken(this);
			}
		}
	}
	
	@Override
	public void replaceInformationControl(final IInformationControlCreator informationPresenterControlCreator,
			final Rectangle contentBounds, final Object information, final Rectangle subjectArea, final boolean takeFocus) {
		if (fTokenOwner != null && fTokenKeeperToReplace != null) {
			fTokenOwner.releaseWidgetToken(fTokenKeeperToReplace);
		}
		super.replaceInformationControl(informationPresenterControlCreator, contentBounds, information, subjectArea, takeFocus);
	}
	
	@Override
	public boolean requestWidgetToken(final IWidgetTokenOwner owner) {
		hideInformationControl();
		if (DEBUG) {
			System.out.println("StickyHoverManager gave up widget token (no prio)"); //$NON-NLS-1$
		}
		return true;
	}
	
	@Override
	public boolean requestWidgetToken(final IWidgetTokenOwner owner, final int priority) {
		if (getCurrentInformationControl2() != null) {
			if (getCurrentInformationControl2().isFocusControl()) {
				if (DEBUG) {
					System.out.println("StickyHoverManager kept widget token (focused)"); //$NON-NLS-1$
				}
				return false;
			} else if (priority > WIDGET_PRIORITY) {
				hideInformationControl();
				if (DEBUG) {
					System.out.println("StickyHoverManager gave up widget token (prio)"); //$NON-NLS-1$
				}
				return true;
			} else {
				if (DEBUG) {
					System.out.println("StickyHoverManager kept widget token (prio)"); //$NON-NLS-1$
				}
				return false;
			}
		}
		if (DEBUG) {
			System.out.println("StickyHoverManager gave up widget token (no iControl)"); //$NON-NLS-1$
		}
		return true;
	}
	
	@Override
	public boolean setFocus(final IWidgetTokenOwner owner) {
		final IInformationControl iControl= getCurrentInformationControl2();
		if (iControl instanceof IInformationControlExtension5) {
			final IInformationControlExtension5 iControl5= (IInformationControlExtension5) iControl;
			if (iControl5.isVisible()) {
				iControl.setFocus();
				return iControl.isFocusControl();
			}
			return false;
		}
		iControl.setFocus();
		return iControl.isFocusControl();
	}
	
}
