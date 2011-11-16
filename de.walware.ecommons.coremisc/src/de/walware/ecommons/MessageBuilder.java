/*******************************************************************************
 * Copyright (c) 2005-2011 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM - Initial API and implementation
 *******************************************************************************/

package de.walware.ecommons;


/**
 * Creates text message like {@link org.eclipse.osgi.util.NLS} bind.
 * 
 * This class is intent to use when creating repeatedly in a single thread.
 * It is not thread safe.
 */
public class MessageBuilder {
	// method implementation from NLS
	
	
	private static final Object[] EMPTY_ARGS = new Object[0];
	
	
	private final StringBuilder fBuilder;
	
	
	public MessageBuilder() {
		this(128);
	}
	
	public MessageBuilder(final int initialBufferLength) {
		fBuilder = new StringBuilder(initialBufferLength);
	}
	
	
	/**
	 * Bind the given message's substitution locations with the given string value.
	 * 
	 * @param message the message to be manipulated
	 * @param binding the object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException if the text appearing within curly braces in the given message does not map to an integer 
	 */
	public String bind(final String message, final Object binding) {
		return internalBind(message, null, String.valueOf(binding), null);
	}
	
	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param binding1 An object to be inserted into the message
	 * @param binding2 A second object to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException if the text appearing within curly braces in the given message does not map to an integer
	 */
	public String bind(final String message, final Object binding1, final Object binding2) {
		return internalBind(message, null, String.valueOf(binding1), String.valueOf(binding2));
	}
	
	/**
	 * Bind the given message's substitution locations with the given string values.
	 * 
	 * @param message the message to be manipulated
	 * @param bindings An array of objects to be inserted into the message
	 * @return the manipulated String
	 * @throws IllegalArgumentException if the text appearing within curly braces in the given message does not map to an integer
	 */
	public String bind(final String message, final Object[] bindings) {
		return internalBind(message, bindings, null, null);
	}
	
	/*
	 * Perform the string substitution on the given message with the specified args.
	 * See the class comment for exact details.
	 */
	private String internalBind(final String message, Object[] args, final String argZero, final String argOne) {
		if (message == null)
		 {
			return "No message available."; //$NON-NLS-1$
		}
		if (args == null || args.length == 0) {
			args = EMPTY_ARGS;
		}
		
		fBuilder.setLength(0);
		
		final int length = message.length();
		{	//estimate correct size of string buffer to avoid growth
			int bufLen = length + (args.length * 5);
			if (argZero != null) {
				bufLen += argZero.length() - 3;
				if (argOne != null) {
					bufLen += argOne.length() - 3;
				}
			}
			fBuilder.ensureCapacity(bufLen);
		}
		for (int i = 0; i < length; i++) {
			final char c = message.charAt(i);
			switch (c) {
				case '{' :
					int index = message.indexOf('}', i);
					// if we don't have a matching closing brace then...
					if (index == -1) {
						fBuilder.append(c);
						break;
					}
					i++;
					if (i >= length) {
						fBuilder.append(c);
						break;
					}
					// look for a substitution
					int number = -1;
					try {
						number = Integer.parseInt(message.substring(i, index));
					} catch (final NumberFormatException e) {
						throw (IllegalArgumentException) new IllegalArgumentException().initCause(e);
					}
					if (number == 0 && argZero != null) {
						fBuilder.append(argZero);
					} else if (number == 1 && argOne != null) {
						fBuilder.append(argOne);
					} else {
						if (number >= args.length || number < 0) {
							fBuilder.append("<missing argument>"); //$NON-NLS-1$
							i = index;
							break;
						}
						fBuilder.append(args[number]);
					}
					i = index;
					break;
				case '\'' :
					// if a single quote is the last char on the line then skip it
					final int nextIndex = i + 1;
					if (nextIndex >= length) {
						fBuilder.append(c);
						break;
					}
					final char next = message.charAt(nextIndex);
					// if the next char is another single quote then write out one
					if (next == '\'') {
						i++;
						fBuilder.append(c);
						break;
					}
					// otherwise we want to read until we get to the next single quote
					index = message.indexOf('\'', nextIndex);
					// if there are no more in the string, then skip it
					if (index == -1) {
						fBuilder.append(c);
						break;
					}
					// otherwise write out the chars inside the quotes
					fBuilder.append(message.substring(nextIndex, index));
					i = index;
					break;
				default :
					fBuilder.append(c);
			}
		}
		return fBuilder.toString();
	}
	
}
