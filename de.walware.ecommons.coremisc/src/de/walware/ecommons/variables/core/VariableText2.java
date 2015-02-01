/*=============================================================================#
 # Copyright (c) 2012-2015 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.variables.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IStringVariable;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.IValueVariable;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.osgi.util.NLS;

import de.walware.ecommons.ECommons;
import de.walware.ecommons.variables.internal.Messages;


/**
 * Provides flexible and fast functions to validate and perform variable string substitution
 * similar to {@link IStringVariableManager}.
 * <ul>
 *   <li>Support of nested variables</li>
 *   <li>Escaping of '$' by '$$'</li>
 *   <li>Additional custom templates</li>
 *   <li>Flexible error reporting by using {@link Severities} and {@link IProblemReporter}</li>
 * </ul>
 */
public class VariableText2 {
	
	
	public static interface IProblemReporter {
		
		void problemFound(IStatus status, int begin, int end) throws CoreException;
		
	}
	
	public static class Severities {
		
		
		private final int fUndefined;
		private final int fUnresolved;
		
		
		public Severities(final int undefined, final int unresolved) {
			fUndefined = undefined;
			fUnresolved = unresolved;
		}
		
		
		public int getUndefined() {
			return fUndefined;
		}
		
		public int getUnresolved() {
			return fUnresolved;
		}
		
	}
	
	public static IProblemReporter ERROR_EXCEPTION_REPORTER = new IProblemReporter() {
		@Override
		public void problemFound(final IStatus status, final int begin, final int end)
				throws CoreException {
			if (status.getSeverity() >= IStatus.ERROR) {
				throw new CoreException(status);
			}
		}
	};
	
	
	public static Severities SYNTAX_SEVERITIES = new Severities(IStatus.WARNING, IStatus.OK);
	
	public static Severities RESOLVE_SEVERITIES = new Severities(IStatus.ERROR, IStatus.ERROR);
	
	
	private final static String DOLLAR_SIGN = "$"; //$NON-NLS-1$
	
	
	private static class VariableReference {
		
		/** Offset of '$' */
		private final int fBegin;
		/** Offset of '}' */
		private int fEnd;
		
		
		/** Name of the variable */
		private final String fName;
		
		private IStringVariable fVariable;
		
		private String fValue;
		
		
		public VariableReference(final int begin, final int end, final String name) {
			fBegin = begin;
			fEnd = end;
			fName = name;
			if (name == DOLLAR_SIGN) {
				fValue = DOLLAR_SIGN;
			}
		}
		
		
		public boolean hasArg() {
			return (fEnd > 0 && fEnd - fBegin > fName.length() + 3);
		}
		
		public int getArgBegin() {
			return fBegin + fName.length() + 3;
		}
		
		public int getArgEnd() {
			return fEnd - 1;
		}
		
	}
	
	
	private final Map<String, IStringVariable> fSpecialVariables;
	
	private final List<VariableReference> fReferences = new ArrayList<VariableReference>();
	
	private final StringBuilder fStringBuilder = new StringBuilder();
	
	
	public VariableText2(final Map<String, IStringVariable> variables) {
		fSpecialVariables = (variables != null) ? variables : Collections.<String, IStringVariable>emptyMap();
	}
	
	
	public void validate(final String text, Severities severities, IProblemReporter reporter) throws CoreException {
		if (severities == null) {
			severities = RESOLVE_SEVERITIES;
		}
		if (reporter == null) {
			reporter = ERROR_EXCEPTION_REPORTER;
		}
		fReferences.clear();
		parseReferences(text, severities, reporter);
		
		if (severities.getUnresolved() == IStatus.OK) {
			return;
		}
		resolveReferences(text, 0, Integer.MAX_VALUE);
	}
	
	public String performStringSubstitution(final String text) throws CoreException {
		fReferences.clear();
		parseReferences(text, RESOLVE_SEVERITIES, ERROR_EXCEPTION_REPORTER);
		
		resolveReferences(text, 0, Integer.MAX_VALUE);
		
		return concatChecked(text, 0, 0, text.length());
	}
	
	private void parseReferences(final String text, final Severities severities,
			final IProblemReporter reporter) throws CoreException {
		final int l = text.length();
		
		for (int offset = 0; offset < l; ) {
			final char c = text.charAt(offset);
			if (c == '$') {
				if (offset + 1 == l) {
					reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
							NLS.bind(Messages.Validation_Syntax_DollarEnd_message, c)),
							offset, offset + 1);
					break;
				}
				
				final char c2 = text.charAt(offset + 1);
				
				if (c2 == '$') {
					fReferences.add(new VariableReference(offset, offset + 2, DOLLAR_SIGN));
					offset += 2;
					continue;
				}
				
				if (c2 == '{') {
					int end = offset + 2;
					PARSE_NAME: for (; end < l; end++) {
						final char cEnd = text.charAt(end);
						if ((cEnd >= 'a' && cEnd <= 'z') || (cEnd >= 'A' && cEnd <= 'Z') || cEnd == '_') {
							continue;
						}
						if (cEnd == ':' || cEnd == '}') {
							if (end == offset + 2) {
								reporter.problemFound(
										new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
												Messages.Validation_Syntax_VarMissingName_message),
												offset, 3);
								break PARSE_NAME;
							}
							else {
								fReferences.add(new VariableReference(
										offset, (cEnd == '}') ? (end + 1) : -1,
												text.substring(offset + 2, end)));
								break PARSE_NAME;
							}
						}
						reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
								NLS.bind(Messages.Validation_Syntax_VarInvalidChar_message,
										cEnd, text.substring(offset + 2, end) )),
								offset, end + 1);
						break PARSE_NAME;
					}
					if (end == l) {
						reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
								NLS.bind(Messages.Validation_Syntax_VarNotClosed_message,
										text.substring(offset + 2, end) )),
								offset, 2);
					}
					offset = end + 1;
					continue;
				}
				
				reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
						NLS.bind(Messages.Validation_Syntax_DollorInvalidChar_message, c)),
						offset, 2);
				offset++;
				continue;
			}
			else if (c == '}') {
				for (int i = fReferences.size() - 1; i >= 0; i--) {
					final VariableReference ref = fReferences.get(i);
					if (ref.fEnd < 0) {
						ref.fEnd = offset + 1;
						break;
					}
				}
				offset++;
				continue;
			}
			else {
				offset++;
				continue;
			}
		}
		
		if (fReferences.isEmpty()) {
			return;
		}
		
		final IStringVariableManager manager = VariablesPlugin.getDefault().getStringVariableManager();
		for (final VariableReference ref : fReferences) {
			if (ref.fName == DOLLAR_SIGN) {
				continue;
			}
			if (ref.fEnd < 0) {
				reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
						NLS.bind(Messages.Validation_Syntax_VarNotClosed_message,
								ref.fName )),
						ref.fBegin, 2);
				continue;
			}
			if ((ref.fVariable = fSpecialVariables.get(ref.fName)) == null
					&& (ref.fVariable = manager.getValueVariable(ref.fName)) == null
					&& (ref.fVariable = manager.getDynamicVariable(ref.fName)) == null) {
				reporter.problemFound(new Status(severities.getUndefined(), ECommons.PLUGIN_ID,
						NLS.bind(Messages.Validation_Ref_VarNotDefined_message,
								ref.fName )),
						ref.fBegin, 2);
			}
		}
		for (final VariableReference ref : fReferences) {
			if (ref.fVariable != null && ref.hasArg()) {
				if (!(ref.fVariable instanceof IDynamicVariable)
						|| !((IDynamicVariable) ref.fVariable).supportsArgument()) {
					reporter.problemFound(new Status(IStatus.ERROR, ECommons.PLUGIN_ID,
							NLS.bind(Messages.Validation_Ref_VarNoArgs_message,
									ref.fName )),
							ref.fBegin, 2 );
				}
			}
		}
	}
	
	private int resolveReferences(final String text, final int refIdx, final int end) throws CoreException {
		final int l = fReferences.size();
		for (int i = refIdx; i < fReferences.size(); i++) {
			final VariableReference ref = fReferences.get(i);
			if (ref.fEnd > end) {
				return i;
			}
			if (ref.fValue != null) {
				continue;
			}
			
			String arg;
			if (ref.hasArg()) {
				if (i + 1 < l && fReferences.get(i + 1).fBegin < ref.fEnd) {
					resolveReferences(text, i + 1, ref.fEnd);
					arg = concat(text, i + 1, ref.getArgBegin(), ref.getArgEnd());
				}
				else {
					arg = text.substring(ref.getArgBegin(), ref.getArgEnd());
				}
			}
			else {
				arg = null;
			}
			
			ref.fValue = (ref.fVariable != null) ? resolve(ref.fVariable, arg) : ""; //$NON-NLS-1$
		}
		return l;
	}
	
	private String concat(final String text, int refIdx, int begin, final int end) {
		fStringBuilder.setLength(0);
		while (refIdx < fReferences.size()) {
			final VariableReference ref = fReferences.get(refIdx++);
			if (ref.fBegin >= begin) {
				fStringBuilder.append(text, begin, ref.fBegin);
				fStringBuilder.append(ref.fValue);
				begin = ref.fEnd;
			}
		}
		fStringBuilder.append(text, begin, end);
		return fStringBuilder.toString();
	}
	
	private String concatChecked(final String text, int refIdx, int begin, final int end) {
		fStringBuilder.setLength(0);
		while (refIdx < fReferences.size()) {
			final VariableReference ref = fReferences.get(refIdx++);
			if (ref.fBegin >= begin) {
				fStringBuilder.append(text, begin, ref.fBegin);
				fStringBuilder.append(checkValue(ref.fVariable, ref.fValue));
				begin = ref.fEnd;
			}
		}
		fStringBuilder.append(text, begin, end);
		return fStringBuilder.toString();
	}
	
	protected String resolve(final IStringVariable variable, final String argument) throws CoreException {
		final String value;
		if (variable instanceof IDynamicVariable) {
			value = ((IDynamicVariable) variable).getValue(argument);
		}
		else {
			value = ((IValueVariable) variable).getValue();
//			if (value != null && value.indexOf("${") >= 0) {
//				value = VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(value, true);
//			}
		}
		return (value != null) ? value : ""; //$NON-NLS-1$
	}
	
	protected String checkValue(final IStringVariable variable, final String value) {
		return value;
	}
	
}
