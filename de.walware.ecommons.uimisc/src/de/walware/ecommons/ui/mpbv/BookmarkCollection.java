/*=============================================================================#
 # Copyright (c) 2009-2016 Stephan Wahlbrink (WalWare.de) and others.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import de.walware.ecommons.preferences.PreferencesUtil;
import de.walware.ecommons.preferences.core.Preference;
import de.walware.ecommons.preferences.core.Preference.StringArrayPref;


public class BookmarkCollection {
	
	
	private static final Map<String, BookmarkCollection> gCollections= new HashMap<>();
	
	public static BookmarkCollection getCollection(final String qualifier) {
		synchronized (gCollections) {
			BookmarkCollection collection= gCollections.get(qualifier);
			if (collection == null) {
				collection= new BookmarkCollection(qualifier);
				gCollections.put(qualifier, collection);
			}
			return collection;
		}
	}
	
	
	private static final char SEPARATOR= '\u001f';
	private static final Pattern SEPARATOR_PATTERN= Pattern.compile(Pattern.quote("\u001f"));
	
	
	private final String qualifier;
	
	private final StringArrayPref pref;
	
	private final List<BrowserBookmark> bookmarks= new ArrayList<>();
	
	
	private BookmarkCollection(final String qualifier) {
		this.qualifier= qualifier;
		this.pref= new Preference.StringArrayPref(qualifier, "bookmarks", Preference.IS2_SEPARATOR_CHAR);
		
		load();
	}
	
	
	public String getQualifier() {
		return this.qualifier;
	}
	
	public List<BrowserBookmark> getBookmarks() {
		return this.bookmarks;
	}
	
	
	private void load() {
		synchronized (this.bookmarks) {
			final String[] strings= PreferencesUtil.getInstancePrefs().getPreferenceValue(this.pref);
			for (final String s : strings) {
				BrowserBookmark bookmark= null;
				final String[] split= SEPARATOR_PATTERN.split(s);
				if (split.length == 2) {
					bookmark= new BrowserBookmark(split[0], split[1]);
				}
				if (bookmark != null) {
					this.bookmarks.add(bookmark);
				}
			}
		}
	}
	
	public void save() {
		synchronized (this.bookmarks) {
			final String[] strings= new String[this.bookmarks.size()];
			for (int i= 0; i < this.bookmarks.size(); i++) {
				final BrowserBookmark bookmark= this.bookmarks.get(i);
				strings[i]= bookmark.getLabel() + SEPARATOR + bookmark.getUrl();
			}
			PreferencesUtil.setPrefValue(PreferencesUtil.getInstancePrefs().getPreferenceContexts().get(0),
					this.pref, strings);
		}
	}
	
}
