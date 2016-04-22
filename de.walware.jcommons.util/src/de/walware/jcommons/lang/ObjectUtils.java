/*=============================================================================#
 # Copyright (c) 2015-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.jcommons.lang;

import java.util.Collection;
import java.util.Formatter;
import java.util.List;
import java.util.Locale;

public class ObjectUtils {
	
	
	public static class ToStringBuilder {
		
		
		private static final String INDENT= "  "; //$NON-NLS-1$
		private static final String CONT_INDENT= INDENT + INDENT;
		
		private static final String PROP_PREFIX= "\n" + INDENT; //$NON-NLS-1$
		private static final String PROP_ASSIGN= "= "; //$NON-NLS-1$
		
		private static final Object NULL= "<null>"; //$NON-NLS-1$
		
		
		private final StringBuilder sb;
		
		private Formatter formatter;
		
		
		public ToStringBuilder() {
			this.sb= new StringBuilder();
		}
		
		public ToStringBuilder(final String name) {
			this.sb= new StringBuilder(name);
		}
		
		public ToStringBuilder(final String name, final Class<?> clazz) {
			this.sb= new StringBuilder(name);
			
			this.sb.append(" ("); //$NON-NLS-1$
			this.sb.append(clazz.getName());
			this.sb.append(")"); //$NON-NLS-1$
		}
		
		
		public StringBuilder getStringBuilder() {
			return this.sb;
		}
		
		public final void append(final String s) {
			this.sb.append(s);
		}
		
		public final void append(final String s, final int begin, final int end) {
			this.sb.append(s, begin, end);
		}
		
		public final void append(final char c) {
			this.sb.append(c);
		}
		
		
		private void appendLines(final String s, final String contPrefix) {
			int start= 0;
			int end= s.indexOf('\n', start);
			
			if (end < 0) {
				append(s);
				return;
			}
			
			if (end > 0 && s.charAt(end - 1) == '\r') {
				append(s, start, end - 1);
			}
			else {
				append(s, start, end);
			}
			start= end + 1;
			while ((end= s.indexOf('\n', start)) >= 0) {
				append(contPrefix);
				if (end > 0 && s.charAt(end - 1) == '\r') {
					append(s, start, end - 1);
				}
				else {
					append(s, start, end);
				}
				start= end + 1;
			}
			if (start < s.length()) {
				append(contPrefix);
				append(s, start, s.length());
			}
		}
		
		public void addProp(final String name) {
			if (name == null) {
				throw new NullPointerException();
			}
			this.sb.append(PROP_PREFIX);
			this.sb.append(name);
			this.sb.append(PROP_ASSIGN);
		}
		
		public void addProp(final String name, final String value) {
			addProp(name);
			
			if (value == null) {
				this.sb.append(NULL);
				return;
			}
			appendLines(value, PROP_PREFIX + CONT_INDENT);
		}
		
		public void addProp(final String name, final Object value) {
			addProp(name);
			
			if (value == null) {
				this.sb.append(NULL);
				return;
			}
			appendLines(value.toString(), PROP_PREFIX + CONT_INDENT);
		}
		
		public void addProp(final String name, final Collection<?> value) {
			addProp(name);
			
			if (value == null) {
				this.sb.append(NULL);
				return;
			}
			if (value instanceof List) {
				this.sb.append('[');
			}
			else {
				this.sb.append('{');
			}
			if (!value.isEmpty()) {
				for (final Object e : value) {
					this.sb.append(PROP_PREFIX + INDENT);
					if (e == null) {
						this.sb.append(NULL);
						continue;
					}
					appendLines(e.toString(), PROP_PREFIX + INDENT + CONT_INDENT); 
				}
				this.sb.append(PROP_PREFIX);
			}
			if (value instanceof List) {
				this.sb.append(']');
			}
			else {
				this.sb.append('}');
			}
		}
		
		public void addProp(final String name, final String valueFormat, final Object... valueArgs) {
			addProp(name);
			
			if (this.formatter == null) {
				this.formatter= new Formatter(this.sb, Locale.ENGLISH);
			}
			this.formatter.format(valueFormat, valueArgs);
		}
		
		public String build() {
			return this.sb.toString();
		}
		
		@Override
		public String toString() {
			return this.sb.toString();
		}
		
	}
	
	
	private ObjectUtils() {}
	
}
