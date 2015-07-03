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

import de.walware.ecommons.variables.internal.core.Messages;


/**
 * Provides flexible and fast functions to validate and perform variable string substitution
 * similar to {@link IStringVariableManager}.
 * <ul>
 *   <li>Support of nested variables</li>
 *   <li>Escaping of '$' by '$$'</li>
 *   <li>Additional extra variables</li>
 *   <li>Flexible error reporting by using {@link Severities} and {@link IProblemReporter}</li>
 * </ul>
 */
public class VariableText2 {
	
	
	public static class Severities {
		
		
		public static final Severities CHECK_SYNTAX= new Severities(IStatus.ERROR, IStatus.OK);
		
		public static final Severities RESOLVE= new Severities(IStatus.ERROR, IStatus.ERROR);
		
		
		private final int undefined;
		private final int unresolved;
		
		
		public Severities(final int undefined, final int unresolved) {
			this.undefined= undefined;
			this.unresolved= unresolved;
		}
		
		
		public int getUndefined() {
			return this.undefined;
		}
		
		public int getUnresolved() {
			return this.unresolved;
		}
		
	}
	
	
	public static interface IProblemReporter {
		
		void problemFound(IStatus status, int begin, int end) throws CoreException;
		
	}
	
	public static final IProblemReporter EXCEPTION_REPORTER= new IProblemReporter() {
		@Override
		public void problemFound(final IStatus status, final int begin, final int end)
				throws CoreException {
			throw new CoreException(status);
		}
	};
	
	
	private static final String DOLLAR_SIGN= "$"; //$NON-NLS-1$
	
	private static final String RESOLVE_FAILED_VALUE= new String("<unresolved>"); //$NON-NLS-1$
	
	
	private static class VariableReference {
		
		/** Offset of '$' */
		private final int begin;
		/** Offset of '}' */
		private int end;
		
		
		/** Name of the variable */
		private final String name;
		
		private IStringVariable variable;
		
		private String value;
		
		
		public VariableReference(final int begin, final int end, final String name) {
			this.begin= begin;
			this.end= end;
			this.name= name;
			if (name == DOLLAR_SIGN) {
				this.value= DOLLAR_SIGN;
			}
		}
		
		
		public boolean hasArg() {
			return (this.end > 0 && this.end - this.begin > this.name.length() + 3);
		}
		
		public int getArgBegin() {
			return this.begin + this.name.length() + 3;
		}
		
		public int getArgEnd() {
			return this.end - 1;
		}
		
		
		@Override
		public String toString() {
			final StringBuilder sb= new StringBuilder("VariableReference"); //$NON-NLS-1$
			sb.append(" name= ").append(this.name); //$NON-NLS-1$
			sb.append("\n\t" + "begin= ").append(this.begin); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("\n\t" + "end= ").append(this.end); //$NON-NLS-1$ //$NON-NLS-2$
			sb.append("\n"); //$NON-NLS-1$
			return sb.toString();
		}
		
	}
	
	
	private final Map<String, IStringVariable> extraVariables;
	
	private Severities severities;
	private IProblemReporter reporter;
	private final List<VariableReference> references= new ArrayList<>();
	
	private IStringVariableManager variableManager;
	private final StringBuilder sb= new StringBuilder();
	
	
	public VariableText2(final Map<String, IStringVariable> variables) {
		this.extraVariables= (variables != null) ? variables : Collections.<String, IStringVariable>emptyMap();
	}
	
	public VariableText2() {
		this(null);
	}
	
	
	public final Map<String, IStringVariable> getExtraVariables() {
		return this.extraVariables;
	}
	
	public void validate(final String text, final Severities severities, final IProblemReporter reporter)
			throws CoreException {
		try {
			this.severities= (severities != null) ? severities : Severities.RESOLVE;
			this.reporter= (reporter != null) ? reporter : EXCEPTION_REPORTER;
			
			parseReferences(text);
			
			if (this.severities.getUnresolved() != IStatus.OK) {
				resolveReferences(text, 0, Integer.MAX_VALUE);
			}
		}
		finally {
			this.severities= null;
			this.reporter= null;
			this.references.clear();
		}
	}
	
	public String performStringSubstitution(final String text, final Severities severities)
			throws CoreException {
		try {
			this.severities= (severities != null) ? severities : Severities.RESOLVE;
			this.reporter= EXCEPTION_REPORTER;
			
			parseReferences(text);
			
			resolveReferences(text, 0, Integer.MAX_VALUE);
			
			return concatChecked(text, 0, 0, text.length());
		}
		finally {
			this.severities= null;
			this.reporter= null;
			this.references.clear();
		}
	}
	
	public String escapeText(final String text) {
		final StringBuilder escaped= getStringBuilder();
		int start= 0;
		for (int idx= 0; idx < text.length(); idx++) {
			if (text.charAt(idx) == '$') {
				if (start < idx) {
					escaped.append(text, start, idx);
				}
				escaped.append("$$"); //$NON-NLS-1$
				start= idx + 1;
			}
		}
		if (start == 0) {
			return text;
		}
		if (start < text.length()) {
			escaped.append(text, start, text.length());
		}
		return escaped.toString();
	}
	
	public final IStringVariable getVariable(final String name) {
		if (name == null) {
			throw new NullPointerException("name"); //$NON-NLS-1$
		}
		IStringVariable variable;
		
		variable= this.extraVariables.get(name);
		
		if (variable == null) {
			if (this.variableManager == null) {
				this.variableManager= VariablesPlugin.getDefault().getStringVariableManager();
			}
			
			variable= this.variableManager.getValueVariable(name);
			
			if (variable == null) {
				variable= this.variableManager.getDynamicVariable(name);
			}
		}
		
		return variable;
	}
	
	
	private StringBuilder getStringBuilder() {
		this.sb.setLength(0);
		return this.sb;
	}
	
	private void parseReferences(final String text) throws CoreException {
		final int l= text.length();
		for (int offset= 0; offset < l; ) {
			final char c= text.charAt(offset);
			if (c == '$') {
				if (offset + 1 == l) {
					this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
							NLS.bind(Messages.Validation_Syntax_DollarEnd_message, c)),
							offset, offset + 1);
					break;
				}
				
				final char c2= text.charAt(offset + 1);
				
				if (c2 == '$') {
					this.references.add(new VariableReference(offset, offset + 2, DOLLAR_SIGN));
					offset += 2;
					continue;
				}
				
				if (c2 == '{') {
					int end= offset + 2;
					PARSE_NAME: for (; end < l; end++) {
						final char cEnd= text.charAt(end);
						if ((cEnd >= 'a' && cEnd <= 'z') || (cEnd >= 'A' && cEnd <= 'Z') || cEnd == '_') {
							continue;
						}
						if (cEnd == ':' || cEnd == '}') {
							if (end == offset + 2) {
								this.reporter.problemFound(
										new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
												Messages.Validation_Syntax_VarMissingName_message),
												offset, 3);
								break PARSE_NAME;
							}
							else {
								this.references.add(new VariableReference(
										offset, (cEnd == '}') ? (end + 1) : -1,
												text.substring(offset + 2, end)));
								break PARSE_NAME;
							}
						}
						this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
								NLS.bind(Messages.Validation_Syntax_VarInvalidChar_message,
										cEnd, text.substring(offset + 2, end) )),
								offset, end + 1);
						break PARSE_NAME;
					}
					if (end == l) {
						this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
								NLS.bind(Messages.Validation_Syntax_VarNotClosed_message,
										text.substring(offset + 2, end) )),
								offset, 2);
					}
					offset= end + 1;
					continue;
				}
				
				this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
						NLS.bind(Messages.Validation_Syntax_DollorInvalidChar_message, c)),
						offset, 2);
				offset++;
				continue;
			}
			else if (c == '}') {
				for (int i= this.references.size() - 1; i >= 0; i--) {
					final VariableReference ref= this.references.get(i);
					if (ref.end < 0) {
						ref.end= offset + 1;
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
		
		if (this.references.isEmpty()) {
			return;
		}
		
		for (final VariableReference ref : this.references) {
			if (ref.name == DOLLAR_SIGN) {
				continue;
			}
			if (ref.end < 0) {
				this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
						NLS.bind(Messages.Validation_Syntax_VarNotClosed_message,
								ref.name )),
						ref.begin, 2 );
				continue;
			}
			if ((ref.variable= getVariable(ref.name)) == null) {
				this.reporter.problemFound(new Status(this.severities.getUndefined(), ECommonsVariablesCore.PLUGIN_ID,
						NLS.bind(Messages.Validation_Ref_VarNotDefined_message,
								ref.name )),
						ref.begin, 2 );
			}
		}
		for (final VariableReference ref : this.references) {
			if (ref.variable != null && ref.hasArg()) {
				if (!(ref.variable instanceof IDynamicVariable)
						|| !((IDynamicVariable) ref.variable).supportsArgument()) {
					this.reporter.problemFound(new Status(IStatus.ERROR, ECommonsVariablesCore.PLUGIN_ID,
							NLS.bind(Messages.Validation_Ref_VarNoArgs_message,
									ref.name )),
							ref.begin, 2 );
				}
			}
		}
	}
	
	private boolean resolveReferences(final String text, final int refIdx, final int end) throws CoreException {
		boolean ok= true;
		for (int i= refIdx; i < this.references.size(); i++) {
			final VariableReference ref= this.references.get(i);
			if (ref.end > end) {
				break;
			}
			try {
				if (ref.value != null) {
					continue;
				}
				
				String arg;
				if (ref.hasArg()) {
					if (i + 1 < this.references.size() && this.references.get(i + 1).begin < ref.end) {
						if (resolveReferences(text, i + 1, ref.end)) {
							arg= concat(text, i + 1, ref.getArgBegin(), ref.getArgEnd());
						}
						else {
							ref.value= RESOLVE_FAILED_VALUE;
							continue;
						}
					}
					else {
						arg= text.substring(ref.getArgBegin(), ref.getArgEnd());
					}
				}
				else {
					arg= null;
				}
				
				if (ref.variable == null) {
					ref.value= RESOLVE_FAILED_VALUE;
					continue;
				}
				
				ref.value= resolve(ref.variable, arg);
			}
			catch (final CoreException e) {
				ref.value= RESOLVE_FAILED_VALUE;
				final IStatus status= e.getStatus();
				this.reporter.problemFound(new Status(this.severities.getUnresolved(),
						status.getPlugin(), status.getMessage() ), ref.begin, ref.end );
			}
			finally {
				if (ref.value == RESOLVE_FAILED_VALUE) {
					ok= false;
				}
			}
		}
		return ok;
	}
	
	private String concat(final String text, int refIdx, int begin, final int end) {
		final StringBuilder newText= getStringBuilder();
		while (refIdx < this.references.size()) {
			final VariableReference ref= this.references.get(refIdx++);
			if (ref.begin >= begin && ref.begin < end) {
				newText.append(text, begin, ref.begin);
				newText.append(ref.value);
				begin= ref.end;
			}
		}
		newText.append(text, begin, end);
		return newText.toString();
	}
	
	private String concatChecked(final String text, int refIdx, int begin, final int end)
			throws CoreException {
		final StringBuilder newText= getStringBuilder();
		while (refIdx < this.references.size()) {
			final VariableReference ref= this.references.get(refIdx++);
			if (ref.begin >= begin && ref.begin < end) {
				newText.append(text, begin, ref.begin);
				newText.append(checkValue(ref.variable, ref.value));
				begin= ref.end;
			}
		}
		newText.append(text, begin, end);
		return newText.toString();
	}
	
	protected String resolve(final IStringVariable variable, final String argument)
			throws CoreException {
		final String value;
		if (variable instanceof IDynamicVariable) {
			value= ((IDynamicVariable) variable).getValue(argument);
		}
		else {
			value= ((IValueVariable) variable).getValue();
//			if (value != null && value.indexOf("${") >= 0) {
//				value= VariablesPlugin.getDefault().getStringVariableManager().performStringSubstitution(value, true);
//			}
		}
		return (value != null) ? value : ""; //$NON-NLS-1$
	}
	
	protected String checkValue(final IStringVariable variable, final String value)
			throws CoreException {
		return value;
	}
	
}
