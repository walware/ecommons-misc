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

import java.util.Formatter;
import java.util.Locale;

public class ObjectUtils {
	
	
	public static class ToStringBuilder {
		
		
		private static final String PROP_PREFIX= "\n  "; //$NON-NLS-1$
		private static final String PROP_ASSIGN= "= "; //$NON-NLS-1$
		
		
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
		
		
		private void appendValueLines(final String s) {
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
				append(PROP_PREFIX + '\t');
				if (end > 0 && s.charAt(end - 1) == '\r') {
					append(s, start, end - 1);
				}
				else {
					append(s, start, end);
				}
				start= end + 1;
			}
			if (start < s.length()) {
				append(PROP_PREFIX + '\t');
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
				this.sb.append("<null>");
				return;
			}
			appendValueLines(value);
		}
		
		public void addProp(final String name, final Object value) {
			addProp(name);
			
			if (value == null) {
				this.sb.append("<null>");
				return;
			}
			appendValueLines(value.toString());
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
	
}
