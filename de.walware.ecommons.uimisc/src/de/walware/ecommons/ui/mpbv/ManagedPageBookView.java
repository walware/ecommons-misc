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

package de.walware.ecommons.ui.mpbv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler2;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.WorkbenchPlugin;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.PageSwitcher;
import org.eclipse.ui.services.IServiceLocator;

import de.walware.jcommons.collections.ImCollections;
import de.walware.jcommons.collections.ImList;

import de.walware.ecommons.ui.SharedUIResources;
import de.walware.ecommons.ui.actions.HandlerCollection;
import de.walware.ecommons.ui.actions.HandlerContributionItem;
import de.walware.ecommons.ui.actions.SimpleContributionItem;
import de.walware.ecommons.ui.util.StatusLineMessageManager;


public abstract class ManagedPageBookView<S extends ISession> extends PageBookView {
	
	
	protected static final String PAGE_CONTROL_MENU_ID= "page_control"; //$NON-NLS-1$
	
	
	private class SessionHandler implements IWorkbenchPart {
		
		
		private final S session;
		
		
		public SessionHandler(final S session) {
			this.session= session;
		}
		
		
		ManagedPageBookView<S> getView() {
			return ManagedPageBookView.this;
		}
		
		@Override
		public IWorkbenchPartSite getSite() {
			return ManagedPageBookView.this.getSite();
		}
		
		public S getSession() {
			return this.session;
		}
		
		
		@Override
		public String getTitle() {
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public Image getTitleImage() {
			return null;
		}
		
		@Override
		public String getTitleToolTip() {
			return ""; //$NON-NLS-1$
		}
		
		@Override
		public void addPropertyListener(final IPropertyListener listener) {
		}
		
		@Override
		public void removePropertyListener(final IPropertyListener listener) {
		}
		
		@Override
		public void createPartControl(final Composite parent) {
		}
		
		@Override
		public void setFocus() {
		}
		
		@Override
		public void dispose() {
		}
		
		@Override
		public Object getAdapter(final Class adapter) {
			return null;
		}
		
		@Override
		public int hashCode() {
			return this.session.hashCode();
		}
		
		@Override
		public boolean equals(final Object obj) {
			return ( (obj instanceof ManagedPageBookView<?>.SessionHandler)
					&& (this.session == ((SessionHandler) obj).session) );
		}
		
	}
	
	private class NewPageHandler extends AbstractHandler {
		
		public NewPageHandler() {
		}
		
		@Override
		public void setEnabled(final Object evaluationContext) {
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			newPage(null, true);
			return null;
		}
		
	}
	
	private class CloseCurrentPageHandler extends AbstractHandler {
		
		public CloseCurrentPageHandler() {
		}
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(!ManagedPageBookView.this.sessionList.isEmpty());
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			final S session= getCurrentSession();
			if (session != null) {
				closePage(session);
			}
			return null;
		}
		
	}
	
	public class CloseAllPagesHandler extends AbstractHandler {
		
		
		@Override
		public void setEnabled(final Object evaluationContext) {
			setBaseEnabled(!ManagedPageBookView.this.sessionList.isEmpty());
		}
		
		@Override
		public Object execute(final ExecutionEvent event) throws ExecutionException {
			ManagedPageBookView.this.sessionHistory.clear();
			final List<S> sessions= getSessions();
			for (final S session : sessions) {
				closePage(session);
			}
			return null;
		}
		
	}
	
	
	private final List<S> sessionList= new ArrayList<>();
	private final Map<S, SessionHandler> sessionMap= new HashMap<>();
	private Comparator<S> sessionComparator;
	
	private final List<S> sessionHistory= new LinkedList<>();
	
	private SessionHandler activeSession;
	
	private final HandlerCollection viewHandlers= new HandlerCollection();
	
	private StatusLineMessageManager statusManager;
	
	
	
	public ManagedPageBookView() {
	}
	
	
	protected void setSessionComparator(final Comparator<S> comparator) {
		this.sessionComparator= comparator;
	}
	
	@Override
	protected boolean isImportant(final IWorkbenchPart part) {
		return ( (part instanceof ManagedPageBookView<?>.SessionHandler)
				&& ((ManagedPageBookView<?>.SessionHandler) part).getView() == this );
	}
	
	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return null;
	}
	
	@Override
	protected void initPage(final IPageBookViewPage page) {
		// E-Bug 473941
		try {
			page.init(new PageSite(getViewSite()) {
				@Override
				public void activate() {
					super.activate();
					
					final IEclipseContext context= (IEclipseContext) getService(IEclipseContext.class);
					if (context != null) {
						context.activate();
					}
				}
				@Override
				public void deactivate() {
					super.deactivate();
					
					final IEclipseContext context= (IEclipseContext) getService(IEclipseContext.class);
					if (context != null) {
						context.deactivate();
					}
				}
			});
		}
		catch (final PartInitException e) {
			WorkbenchPlugin.log(getClass(), "initPage", e); //$NON-NLS-1$
		}
	}
	
	@Override
	public void createPartControl(final Composite parent) {
		super.createPartControl(parent);
		
		final IViewSite site= getViewSite();
		this.statusManager= new StatusLineMessageManager(site.getActionBars().getStatusLineManager());
		initActions(site, this.viewHandlers);
		initPageSwitcher();
		contributeToActionBars(site, site.getActionBars(), this.viewHandlers);
		
		updateState();
	}
	
	@Override
	protected IPage createDefaultPage(final PageBook book) {
		final MessagePage page= new MessagePage();
		page.createControl(getPageBook());
		initPage(page);
		return page;
	}
	
	@Override
	protected PageRec doCreatePage(final IWorkbenchPart part) {
		final SessionHandler sessionHandler= (SessionHandler) part;
		final S session= sessionHandler.getSession();
		
		final IPageBookViewPage page= doCreatePage(session);
		if (page != null) {
			initPage(page);
			page.createControl(getPageBook());
			
			final PageRec pageRecord= new PageRec(part, page);
			return pageRecord;
		}
		return null;
	}
	
	protected /* abstract */ IPageBookViewPage doCreatePage(final S session) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	protected void showPageRec(final PageRec pageRec) {
		if ((this.activeSession != null) ? (pageRec.part != this.activeSession) : (pageRec.part != null)) {
			onPageHiding((IPageBookViewPage) getCurrentPage(), (this.activeSession != null) ? this.activeSession.getSession() : null);
			this.activeSession= null;
			
			super.showPageRec(pageRec);
			
			this.activeSession= (SessionHandler) pageRec.part;
			
			final S session;
			if (this.activeSession != null) {
				session= this.activeSession.getSession();
				this.sessionHistory.remove(session);
				this.sessionHistory.add(0, session);
			}
			else {
				session= null;
			}
			onPageShowing((IPageBookViewPage) pageRec.page, session);
		}
		updateTitle();
	}
	
	protected void updateTitle() {
		final S session= getCurrentSession();
		if (session == null) {
			setContentDescription(getNoPageTitle());
		}
		else {
			setContentDescription(session.getLabel());
		}
	}
	
	protected String getNoPageTitle() {
		return "No page at this time.";
	}
	
	@Override
	public void partClosed(final IWorkbenchPart part) {
		if (part instanceof ManagedPageBookView<?>.SessionHandler) {
			final SessionHandler sessionHandler= (SessionHandler) part;
			final S session= sessionHandler.getSession();
			
			this.sessionList.remove(session);
			this.sessionHistory.remove(session);
			
			if (this.activeSession == part) {
				if (!this.sessionHistory.isEmpty()) {
					showPage(this.sessionHistory.get(0));
				}
				else if (!this.sessionList.isEmpty()) {
					showPage(this.sessionList.get(this.sessionList.size()-1));
				}
			}
			super.partClosed(part);
		}
	}
	
	@Override
	protected void doDestroyPage(final IWorkbenchPart part, final PageRec pageRecord) {
		final SessionHandler sessionHandler= (SessionHandler) part;
		final S session= sessionHandler.getSession();
		
		pageRecord.page.dispose();
		pageRecord.dispose();
		
		this.sessionMap.remove(session);
		if (sessionHandler == this.activeSession) {
			this.activeSession= null;
		}
	}
	
	
	private void initPageSwitcher() {
		new PageSwitcher(this) {
			@Override
			public Object[] getPages() {
				return ManagedPageBookView.this.sessionList.toArray();
			}
			@Override
			public String getName(final Object page) {
				return ((S) page).getLabel();
			}
			@Override
			public ImageDescriptor getImageDescriptor(final Object page) {
				return ((S) page).getImageDescriptor();
			}
			@Override
			public int getCurrentPageIndex() {
				return (activeSession != null) ?
						ManagedPageBookView.this.sessionList.indexOf(
								ManagedPageBookView.this.activeSession.getSession() ) :
						-1;
			}
			@Override
			public void activatePage(final Object page) {
				showPage((S) page);
			}
		};
	}
	
	
	protected boolean getPageControlByUser() {
		return true;
	}
	
	protected void initActions(final IServiceLocator serviceLocator, final HandlerCollection handlers) {
		final IHandlerService handlerService= (IHandlerService) serviceLocator.getService(IHandlerService.class);
		
		if (getPageControlByUser()) {
			final IHandler2 newPageHandler= createNewPageHandler();
			if (newPageHandler != null) {
				handlers.add(SharedUIResources.NEW_PAGE_COMMAND_ID, newPageHandler);
				handlerService.activateHandler(SharedUIResources.NEW_PAGE_COMMAND_ID, newPageHandler);
			}
			final IHandler2 closePageHandler= new CloseCurrentPageHandler();
			handlers.add(SharedUIResources.CLOSE_PAGE_COMMAND_ID, closePageHandler);
			handlerService.activateHandler(SharedUIResources.CLOSE_PAGE_COMMAND_ID, closePageHandler);
			final IHandler2 closeAllPagesHandler= new CloseAllPagesHandler();
			handlers.add(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, closeAllPagesHandler);
			handlerService.activateHandler(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, closeAllPagesHandler);
		}
	}
	
	protected IHandler2 createNewPageHandler() {
		return new NewPageHandler();
	}
	
	protected void contributeToActionBars(final IServiceLocator serviceLocator,
			final IActionBars actionBars, final HandlerCollection handlers) {
		final IToolBarManager toolBarManager= actionBars.getToolBarManager();
		
		toolBarManager.add(new Separator(SharedUIResources.ADDITIONS_MENU_ID));
		toolBarManager.add(new Separator(PAGE_CONTROL_MENU_ID));
		{	final IHandler2 handler= handlers.get(SharedUIResources.NEW_PAGE_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.NEW_PAGE_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
		toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
				new SimpleContributionItem(
						SharedUIResources.getImages().getDescriptor(SharedUIResources.LOCTOOL_CHANGE_PAGE_IMAGE_ID), null,
						"Pages", null,
						SimpleContributionItem.STYLE_PULLDOWN) {
			{
				setId("page_control.change_page"); //$NON-NLS-1$
			}
			@Override
			protected void dropDownMenuAboutToShow(final IMenuManager manager) {
				manager.add(new ShowPageDropdownContribution<>(ManagedPageBookView.this));
			}
			@Override
			protected void execute() throws ExecutionException {
				if (ManagedPageBookView.this.sessionHistory.size() >= 2) {
					showPage(ManagedPageBookView.this.sessionHistory.get(1));
				}
			}
		});
		{	final IHandler2 handler= handlers.get(SharedUIResources.CLOSE_PAGE_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.CLOSE_PAGE_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
		{	final IHandler2 handler= handlers.get(SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID);
			if (handler != null) {
				toolBarManager.appendToGroup(PAGE_CONTROL_MENU_ID,
						new HandlerContributionItem(new CommandContributionItemParameter(
								serviceLocator, null, SharedUIResources.CLOSE_ALL_PAGES_COMMAND_ID, HandlerContributionItem.STYLE_PUSH),
								handler));
			}
		}
	}
	
	@Override
	public void dispose() {
		super.dispose();
		
		this.viewHandlers.dispose();
	}
	
	
	public IPage newPage(S session, final boolean show) {
		session= checkNewSession(session);
		if (session == null || this.sessionList.contains(session)) {
			return null;
		}
		final SessionHandler sessionHandler= new SessionHandler(session);
		if (this.sessionComparator != null) {
			final int idx= Collections.binarySearch(this.sessionList, session, this.sessionComparator);
			this.sessionList.add((idx >= 0) ? idx : -(idx+1), session);
		}
		else {
			this.sessionList.add(session);
		}
		this.sessionMap.put(session, sessionHandler);
		if (show) {
			partActivated(sessionHandler);
			final PageRec pageRec= getPageRec(sessionHandler);
			if (pageRec != null) {
				return pageRec.page;
			}
			else {
				this.sessionMap.remove(sessionHandler);
				this.sessionList.remove(session);
				return null;
			}
		}
		else {
			return null;
		}
	}
	
	protected S checkNewSession(final S session) {
		return session;
	}
	
	public IPage getPage(final S session) {
		final SessionHandler sessionHandler= this.sessionMap.get(session);
		if (sessionHandler != null) {
			final PageRec pageRec= getPageRec(sessionHandler);
			if (pageRec != null) {
				return pageRec.page;
			}
		}
		return null;
	}
	
	public void showPage(final S session) {
		final SessionHandler sessionHandler= this.sessionMap.get(session);
		if (sessionHandler != null) {
			partActivated(sessionHandler);
		}
	}
	
	public void closePage(final S session) {
		final SessionHandler sessionHandler= this.sessionMap.get(session);
		if (sessionHandler != null) {
			partClosed(sessionHandler);
		}
	}
	
	public final ImList<S> getSessions() {
		return ImCollections.toList(this.sessionList);
	}
	
	public final S getCurrentSession() {
		final SessionHandler sessionHandler= this.activeSession;
		if (sessionHandler != null) {
			return sessionHandler.getSession();
		}
		return null;
	}
	
	protected void onPageHiding(final IPageBookViewPage page, final S session) {
		updateState();
	}
	
	protected void onPageShowing(final IPageBookViewPage page, final S session) {
		updateState();
	}
	
	protected StatusLineMessageManager getStatusManager() {
		return this.statusManager;
	}
	
	protected void updateState() {
		this.viewHandlers.update(null);
	}
	
}
