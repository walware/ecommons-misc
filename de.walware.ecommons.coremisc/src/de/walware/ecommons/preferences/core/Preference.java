/*=============================================================================#
 # Copyright (c) 2006-2016 Stephan Wahlbrink (WalWare.de) and others.
 # All rights reserved. This program and the accompanying materials
 # are made available under the terms of the Eclipse Public License v1.0
 # which accompanies this distribution, and is available at
 # http://www.eclipse.org/legal/epl-v10.html
 # 
 # Contributors:
 #     Stephan Wahlbrink - initial API and implementation
 #=============================================================================*/

package de.walware.ecommons.preferences.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

import com.ibm.icu.text.Collator;
import com.ibm.icu.text.RuleBasedCollator;

import de.walware.jcommons.collections.ImCollections;


/**
 * Representing a single preference.
 * <p>
 * This package should help to manage the new preference system
 * with scopes and nodes introduced with Eclipse 3.1.
 * 
 * @param <T> the type, which this preference can store
 *     (normally, thats the same as the type property, but not have to be)
 */
public abstract class Preference<T> {
	
	
	private static Collator DEFAULT_COLLATOR= Collator.getInstance(Locale.ENGLISH);
	static {
		((RuleBasedCollator) DEFAULT_COLLATOR).setUpperCaseFirst(true);
	}
	
	
/*-- Definition --------------------------------------------------------------*/
	
	private final String qualifier;
	private final String key;
	
	
	protected Preference(final String qualifier, final String key) {
		this.qualifier= qualifier;
		this.key= key;
	}
	
	
	public String getQualifier() {
		return this.qualifier;
	}
	
	public String getKey() {
		return this.key;
	}
	
	public abstract Class<T> getUsageType();
	
	/**
	 * Converts object of type T (this Preference is designed for) in the value for the PreferenceStore
	 * 
	 * @param usageValue
	 * @return
	 */
	public abstract String usage2Store(T usageValue);
	
	/**
	 * Converts the value from the PreferenceStore into a object of type T (this Preference is designed for).
	 * 
	 * @param storeValue
	 * @return
	 */
	public abstract T store2Usage(String storeValue);
	
	
	@Override
	public String toString() {
		return this.qualifier + '/' + this.key;
	}
	
	@Override
	public int hashCode() {
		return this.qualifier.hashCode() + this.key.hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (getClass() == obj.getClass()) {
			final Preference<?> other= (Preference<?>) obj;
			return (this.qualifier.equals(other.getQualifier())
					&& this.key.equals(other.getQualifier()) );
		}
		return false;
	}
	
	
/*-- Implementation for common types -----------------------------------------*/	
	
	/**
	 * Default separator for list preferences
	 */
	public static final char LIST_SEPARATOR_CHAR= ',';
	protected static final Pattern LIST_SEPARATOR_PATTERN= Pattern.compile(","); //$NON-NLS-1$
	/**
	 * Separator for file list preferences
	 */
	public static final char IS2_SEPARATOR_CHAR= '\u001e';
	protected static final Pattern IS2_SEPARATOR_PATTERN= Pattern.compile("\u001e"); //$NON-NLS-1$ 
	
	public static final char IS1_SEPARATOR_CHAR= '\u001f';
	protected static final Pattern IS1_SEPARATOR_PATTERN= Pattern.compile("\u001f"); //$NON-NLS-1$ 
	
	
	/**
	 * Default implementation for preferences of type String
	 */
	public static final class StringPref extends Preference<String> {
		
		public static final String DEFAULT_VALUE= new String(""); //$NON-NLS-1$
		
		
		public StringPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<String> getUsageType() {
			return String.class;
		}
		
		@Override
		public String store2Usage(final String storeValue) {
			if (storeValue != null) {
				return storeValue;
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final String usageValue) {
			return usageValue;
		}
		
	}
	
	/**
	 * Default implementation for preferences of type String
	 * 
	 * Usage value can be null
	 */
	public static final class StringPref2 extends Preference<String> {
		
		public StringPref2(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<String> getUsageType() {
			return String.class;
		}
		
		@Override
		public String store2Usage(final String storeValue) {
			return storeValue;
		}
		
		@Override
		public String usage2Store(final String usageValue) {
			return usageValue;
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Boolean/boolean
	 */
	public static final class BooleanPref extends Preference<Boolean> {
		
		public static final Boolean DEFAULT_VALUE= new Boolean(false);
		
		
		public BooleanPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Boolean> getUsageType() {
			return Boolean.class;
		}
		
		@Override
		public Boolean store2Usage(final String storeValue) {
			if (storeValue != null) {
				return Boolean.valueOf(storeValue);
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final Boolean usageValue) {
			return usageValue.toString();
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Integer/int
	 */
	public static final class IntPref extends Preference<Integer> {
		
		public static final Integer DEFAULT_VALUE= new Integer(0);
		
		
		public IntPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Integer> getUsageType() {
			return Integer.class;
		}
		
		@Override
		public Integer store2Usage(final String storeValue) {
			if (storeValue != null) {
				try {
					return Integer.valueOf(storeValue);
				}
				catch (final NumberFormatException e) {}
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final Integer usageValue) {
			return usageValue.toString();
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Long/long
	 */
	public static final class LongPref extends Preference<Long> {
		
		public static final Long DEFAULT_VALUE= new Long(0L);
		
		
		public LongPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Long> getUsageType() {
			return Long.class;
		}
		
		@Override
		public Long store2Usage(final String storeValue) {
			if (storeValue != null) {
				try {
					return Long.valueOf(storeValue);
				}
				catch (final NumberFormatException e) {}
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final Long usageValue) {
			return usageValue.toString();
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Float/float
	 */
	public static final class FloatPref extends Preference<Float> {
		
		public static final Float DEFAULT_VALUE= new Float(0.0f);
		
		
		public FloatPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Float> getUsageType() {
			return Float.class;
		}
		
		@Override
		public Float store2Usage(final String storeValue) {
			if (storeValue != null) {
				try {
					return Float.valueOf(storeValue);
				}
				catch (final NumberFormatException e) {}
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final Float usageValue) {
			return usageValue.toString();
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Double/double
	 */
	public static final class DoublePref extends Preference<Double> {
		
		public static final Double DEFAULT_VALUE= new Double(0.0);
		
		
		public DoublePref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Double> getUsageType() {
			return Double.class;
		}
		
		@Override
		public Double store2Usage(final String storeValue) {
			if (storeValue != null) {
				try {
					return Double.valueOf(storeValue);
				}
				catch (final NumberFormatException e) {}
			}
			return DEFAULT_VALUE;
		}
		
		@Override
		public String usage2Store(final Double usageValue) {
			return usageValue.toString();
		}
		
	}
	
	
	/**
	 * Default implementation for preferences of type Enum
	 */
	public static class EnumPref<E extends Enum<E>> extends Preference<E> {
		
		private final Class<E> enumType;
		
		public EnumPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key);
			this.enumType= enumType;
		}
		
		@Override
		public Class<E> getUsageType() {
			return this.enumType;
		}
		@Override
		public E store2Usage(final String storeValue) {
			if (storeValue != null) {
				try {
					return Enum.valueOf(this.enumType, storeValue);
				}
				catch (final IllegalArgumentException e) {}
			}
			return null;
		}
		
		@Override
		public String usage2Store(final E usageValue) {
			return usageValue.name();
		}
		
	}
	
	/**
	 * Default implementation for preferences of type EnumSet.
	 */
	public static class EnumSetPref<E extends Enum<E>> extends Preference<EnumSet<E>> {
		
		private final Class<E> enumType;
		
		public EnumSetPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key);
			this.enumType= enumType;
		}
		
		@Override
		public Class getUsageType() {
			return EnumSet.class;
		}
		
		@Override
		public EnumSet<E> store2Usage(final String storeValue) {
			final EnumSet<E> set= EnumSet.noneOf(this.enumType);
			if (storeValue != null && !storeValue.isEmpty()) {
				final String[] values= LIST_SEPARATOR_PATTERN.split(storeValue);
				for (final String name : values) {
					if (name.length() > 0) {
						set.add(Enum.valueOf(this.enumType, name));
					}
				}
			}
			return set;
		}
		
		@Override
		public String usage2Store(final EnumSet<E> usageValue) {
			if (usageValue.isEmpty()) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb= new StringBuilder();
			for (final E e : usageValue) {
				sb.append(e.name());
				sb.append(LIST_SEPARATOR_CHAR);
			}
			return sb.substring(0, sb.length() - 1);
		}
		
	}
	
	/**
	 * Default implementation for preferences of type List&lt;Enum&gt;
	 */
	public static class EnumListPref<E extends Enum<E>> extends Preference<List<E>> {
		
		private final Class<E> enumType;
		
		public EnumListPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key);
			this.enumType= enumType;
		}
		
		@Override
		public Class getUsageType() {
			return List.class;
		}
		
		@Override
		public List<E> store2Usage(final String storeValue) {
			if (storeValue != null && !storeValue.isEmpty()) {
				final String[] values= LIST_SEPARATOR_PATTERN.split(storeValue);
				final ArrayList<E> list= new ArrayList<>(values.length);
				for (final String name : values) {
					if (name.length() > 0) {
						try {
							list.add(Enum.valueOf(this.enumType, name));
						}
						catch (final IllegalArgumentException e) {}
					}
				}
				return list;
			}
			return ImCollections.<E>emptyList();
		}
		
		@Override
		public String usage2Store(final List<E> usageValue) {
			if (usageValue.isEmpty()) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb= new StringBuilder();
			for (final E e : usageValue) {
				sb.append(e.name());
				sb.append(LIST_SEPARATOR_CHAR);
			}
			return sb.substring(0, sb.length() - 1);
		}
		
	}
	
	/**
	 * Default implementation for preferences of type String-Array
	 */
	public static class StringArrayPref extends Preference<String[]> {
		
		private static final String[] EMPTY_ARRAY= new String[0];
		
		private final char separator;
		
		public StringArrayPref(final String qualifier, final String key) {
			super(qualifier, key);
			this.separator= LIST_SEPARATOR_CHAR;
		}
		
		public StringArrayPref(final String qualifier, final String key, final char separator) {
			super(qualifier, key);
			this.separator= separator;
		}
		
		@Override
		public Class<String[]> getUsageType() {
			return String[].class;
		}
		
		@Override
		public String[] store2Usage(final String storeValue) {
			if (storeValue != null && !storeValue.isEmpty()) {
				switch (this.separator) {
				case LIST_SEPARATOR_CHAR:
					return LIST_SEPARATOR_PATTERN.split(storeValue);
				case IS2_SEPARATOR_CHAR:
					return IS2_SEPARATOR_PATTERN.split(storeValue);
				default:
					return ((this.separator == LIST_SEPARATOR_CHAR) ?
							LIST_SEPARATOR_PATTERN :
							Pattern.compile("\\Q"+this.separator+"\\E")).split(storeValue);
				}
			}
			return EMPTY_ARRAY;
		}
		
		@Override
		public String usage2Store(final String[] usageValue) {
			if (usageValue.length == 0) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb= new StringBuilder();
			for (int i= 0; i < usageValue.length; i++) {
				sb.append(usageValue[i]);
				sb.append(this.separator);
			}
			return sb.substring(0, sb.length() - 1);
		}
		
	}
	
	/**
	 * Default implementation for preferences of type String-Set
	 */
	public static class StringSetPref extends Preference<Set<String>> {
		
		public StringSetPref(final String qualifier, final String key) {
			super(qualifier, key);
		}
		
		@Override
		public Class<Set<String>> getUsageType() {
			final Object o= Set.class;
			return (Class<Set<String>>) o;
		}
		
		@Override
		public Set<String> store2Usage(final String storeValue) {
			if (storeValue != null && !storeValue.isEmpty()) {
				final String[] strings= LIST_SEPARATOR_PATTERN.split(storeValue);
				return (strings.length <= 16) ?
						ImCollections.newSet(strings) :
						new HashSet<>(ImCollections.newList(strings));
			}
			return ImCollections.emptySet();
		}
		
		@Override
		public String usage2Store(final Set<String> usageValue) {
			if (usageValue.isEmpty()) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb= new StringBuilder();
			final String[] array= usageValue.toArray(new String[usageValue.size()]);
			Arrays.sort(array, DEFAULT_COLLATOR);
//			{	// Debug
//				System.out.print(getKey());
//				System.out.print("= \"");
//				int first= 0;
//				for (int i= 0; i < array.length; i++) {
//					int thisFirst= Character.toUpperCase(array[i].charAt(0));
//					if (thisFirst != first) {
//						first= thisFirst;
//						System.out.print("\"\n//                  \"");
//					}
//					System.out.print(array[i]);
//					System.out.print(',');
//				}
//				System.out.println("\"");
//			}
			for (int i= 0; i < array.length; i++) {
				sb.append(array[i]);
				sb.append(',');
			}
			return sb.substring(0, sb.length() - 1);
		}
		
	}
	
}
