/*******************************************************************************
 * Copyright (c) 2006-2011 WalWare/StatET-Project (www.walware.de/goto/statet)
 * and others. All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Stephan Wahlbrink - initial API and implementation
 *******************************************************************************/

package de.walware.ecommons.preferences;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import de.walware.ecommons.collections.ConstList;


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
	
	/**
	 * The types inside the Eclipse preference store
	 */
	public static enum Type { 
		STRING, 
		BOOLEAN, 
		DOUBLE,
		FLOAT,
		LONG,
		INT;
	}
	
	/**
	 * The default-default value for boolean properties (<code>false</code>).
	 */
	public static final boolean BOOLEAN_DEFAULT_VALUE = false;
	public static final Boolean BOOLEAN_DEFAULT_OBJECT = Boolean.FALSE;
	
	/**
	 * The default-default value for double properties (<code>0.0</code>).
	 */
	public static final double DOUBLE_DEFAULT_VALUE = 0.0;
	public static final Double DOUBLE_DEFAULT_OBJECT = Double.valueOf(0.0);
	
	/**
	 * The default-default value for float properties (<code>0.0f</code>).
	 */
	public static final float FLOAT_DEFAULT_VALUE = 0.0f;
	public static final Float FLOAT_DEFAULT_OBJECT = Float.valueOf(0.0f);
	
	/**
	 * The default-default value for int properties (<code>0</code>).
	 */
	public static final int INT_DEFAULT_VALUE = 0;
	public static final Integer INT_DEFAULT_OBJECT = Integer.valueOf(0);
	
	/**
	 * The default-default value for long properties (<code>0L</code>).
	 */
	public static final long LONG_DEFAULT_VALUE = 0L;
	public static final Long LONG_DEFAULT_OBJECT = Long.valueOf(LONG_DEFAULT_VALUE);
	
	
/*-- Definition --------------------------------------------------------------*/
	
	private final String fQualifier;
	private final String fKey;
	private final Type fType;
	
	
	protected Preference(final String qualifier, final String key, final Type type) {
		
		fQualifier = qualifier;
		fKey = key;
		fType = type;
	}
	
	
	public String getQualifier() {
		return fQualifier;
	}
	
	public String getKey() {
		return fKey;
	}
	
	public Type getStoreType() {
		return fType;
	}
	
	public abstract Class<T> getUsageType();
	
	/**
	 * Converts object of type T (this Preference is designed for) in the value for the PreferenceStore (type specified by getStoreType)
	 * 
	 * @param obj
	 * @return
	 */
	public Object usage2Store(final T obj) {
		return obj;
	}
	
	/**
	 * Converts the value from the PreferenceStore into a object of type T (this Preference is designed for).
	 * 
	 * Impl. note: should always handle storage-objects of type StoreType and String
	 * 
	 * @param obj
	 * @return
	 */
	public abstract T store2Usage(Object obj);
	
	
	@Override
	public String toString() {
		return fQualifier + '/' + fKey;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
	
	@Override
	public boolean equals(final Object obj) {
		if (obj != null && obj.getClass() == getClass()) {
			return obj.toString().equals(toString());
		}
		return false;
	}
	
	
/*-- Implementation for common types -----------------------------------------*/	
	
	/**
	 * Default separator for list preferences
	 */
	public static final char LIST_SEPARATOR_CHAR = ',';
	private static final Pattern LIST_SEPARATOR_PATTERN = Pattern.compile(","); //$NON-NLS-1$
	/**
	 * Separator for file list preferences
	 */
	public static final char IS2_SEPARATOR_CHAR = '\u001e';
	private static final Pattern IS2_SEPARATOR_PATTERN = Pattern.compile("\u001e"); //$NON-NLS-1$ 
	
	
	/**
	 * Default implementation for preferences of type String
	 */
	public static class StringPref extends Preference<String> {
		
		public StringPref(final String qualifier, final String key) {
			super(qualifier, key, Type.STRING);
		}
		
		@Override
		public Class<String> getUsageType() {
			return String.class;
		}
		@Override
		public String store2Usage(final Object obj) {
			if (obj == null) {
				return ""; //$NON-NLS-1$
			}
			return (String) obj;
		}
		
	}
	
	/**
	 * Default implementation for preferences of type String
	 * 
	 * Usage value can be null
	 */
	public static class StringPref2 extends Preference<String> {
		
		public StringPref2(final String qualifier, final String key) {
			super(qualifier, key, Type.STRING);
		}
		
		@Override
		public Class<String> getUsageType() {
			return String.class;
		}
		@Override
		public String store2Usage(final Object obj) {
			return (String) obj;
		}
		
	}
	
	/**
	 * Default implementation for preferences of type Boolean/boolean
	 */
	public static class BooleanPref extends Preference<Boolean> {
		
		public BooleanPref(final String qualifier, final String key) {
			super(qualifier, key, Type.BOOLEAN);
		}
		
		@Override
		public Class<Boolean> getUsageType() {
			return Boolean.class;
		}
		@Override
		public Boolean store2Usage(final Object obj) {
			if (obj != null) {
				if (obj instanceof Boolean) {
					return (Boolean) obj;
				}
				if (obj instanceof String) {
					return Boolean.valueOf((String) obj);
				}
			}
			return BOOLEAN_DEFAULT_OBJECT;
		}
	}
	
	/**
	 * Default implementation for preferences of type Integer/int
	 */
	public static class IntPref extends Preference<Integer> {
		
		public IntPref(final String qualifier, final String key) {
			super(qualifier, key, Type.INT);
		}
		
		@Override
		public Class<Integer> getUsageType() {
			return Integer.class;
		}
		@Override
		public Integer store2Usage(final Object obj) {
			if (obj != null) {
				if (obj instanceof Integer) {
					return (Integer) obj;
				}
				if (obj instanceof String) {
					return Integer.valueOf((String) obj);
				}
			}
			return INT_DEFAULT_OBJECT;
		}
	}
	
	/**
	 * Default implementation for preferences of type Long/long
	 */
	public static class LongPref extends Preference<Long> {
		
		public LongPref(final String qualifier, final String key) {
			super(qualifier, key, Type.LONG);
		}
		
		@Override
		public Class<Long> getUsageType() {
			return Long.class;
		}
		@Override
		public Long store2Usage(final Object obj) {
			if (obj != null) {
				if (obj instanceof Long) {
					return (Long) obj;
				}
				if (obj instanceof String) {
					return Long.valueOf((String) obj);
				}
			}
			return LONG_DEFAULT_OBJECT;
		}
	}
	
	/**
	 * Default implementation for preferences of type Float/float
	 */
	public static class FloatPref extends Preference<Float> {
		
		public FloatPref(final String qualifier, final String key) {
			super(qualifier, key, Type.FLOAT);
		}
		
		@Override
		public Class<Float> getUsageType() {
			return Float.class;
		}
		@Override
		public Float store2Usage(final Object obj) {
			if (obj != null) {
				if (obj instanceof Float) {
					return (Float) obj;
				}
				if (obj instanceof String) {
					return Float.valueOf((String) obj);
				}
			}
			return FLOAT_DEFAULT_OBJECT;
		}
	}
	
	/**
	 * Default implementation for preferences of type Double/double
	 */
	public static class DoublePref extends Preference<Double> {
		
		public DoublePref(final String qualifier, final String key) {
			super(qualifier, key, Type.DOUBLE);
		}
		
		@Override
		public Class<Double> getUsageType() {
			return Double.class;
		}
		@Override
		public Double store2Usage(final Object storedValue) {
			if (storedValue != null) {
				if (storedValue instanceof Double) {
					return (Double) storedValue;
				}
				if (storedValue instanceof String) {
					return Double.valueOf((String) storedValue);
				}
			}
			return DOUBLE_DEFAULT_OBJECT;
		}
	}
	
	/**
	 * Default implementation for preferences of type Enum
	 */
	public static class EnumPref<E extends Enum<E>> extends Preference<E> {
		
		private final Class<E> fEnumType;
		
		public EnumPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key, Type.STRING);
			fEnumType = enumType;
		}
		
		@Override
		public Class<E> getUsageType() {
			return fEnumType;
		}
		@Override
		public E store2Usage(final Object storedValue) {
			final String s = (String) storedValue;
			if (s == null || s.length() == 0) {
				return null;
			}
			return Enum.valueOf(fEnumType, s);
		}
		@Override
		public String usage2Store(final E value) {
			return value.name();
		}
	}
	
	/**
	 * Default implementation for preferences of type EnumSet.
	 */
	public static class EnumSetPref<E extends Enum<E>> extends Preference<EnumSet<E>> {
		
		private final Class<E> fEnumType;
		
		public EnumSetPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key, Type.STRING);
			fEnumType = enumType;
		}
		
		@Override
		public Class getUsageType() {
			return EnumSet.class;
		}
		@Override
		public EnumSet<E> store2Usage(final Object storedValue) {
			final String s = (String) storedValue;
			if (s == null || s.length() == 0) {
				return EnumSet.noneOf(fEnumType);
			}
			final EnumSet<E> set = EnumSet.noneOf(fEnumType);
			if (storedValue != null) {
				final String[] values = LIST_SEPARATOR_PATTERN.split(s);
				for (final String name : values) {
					if (name.length() > 0) {
						set.add(Enum.valueOf(fEnumType, name));
					}
				}
			}
			return set;
		}
		
		@Override
		public String usage2Store(final EnumSet<E> set) {
			if (set.isEmpty()) {
				return ""; //$NON-NLS-1$
			}
			
			final StringBuilder sb = new StringBuilder();
			for (final E e : set) {
				sb.append(e.name());
				sb.append(LIST_SEPARATOR_CHAR);
			}
			return sb.substring(0, sb.length()-1);
		}
	}
	
	/**
	 * Default implementation for preferences of type List&lt;Enum&gt;
	 */
	public static class EnumListPref<E extends Enum<E>> extends Preference<List<E>> {
		
		private final Class<E> fEnumType;
		
		public EnumListPref(final String qualifier, final String key, final Class<E> enumType) {
			super(qualifier, key, Type.STRING);
			fEnumType = enumType;
		}
		
		@Override
		public Class getUsageType() {
			return List.class;
		}
		@Override
		public ArrayList<E> store2Usage(final Object storedValue) {
			final String s = (String) storedValue;
			if (s == null || s.length() == 0) {
				return new ArrayList<E>(0);
			}
			final String[] values = LIST_SEPARATOR_PATTERN.split(s);
			final ArrayList<E> list = new ArrayList<E>(values.length);
			for (final String name : values) {
				if (name.length() > 0) {
					list.add(Enum.valueOf(fEnumType, name));
				}
			}
			return list;
		}
		@Override
		public String usage2Store(final List<E> list) {
			if (list.isEmpty()) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb = new StringBuilder();
			for (final E e : list) {
				sb.append(e.name());
				sb.append(LIST_SEPARATOR_CHAR);
			}
			return sb.substring(0, sb.length()-1);
		}
	}
	
	/**
	 * Default implementation for preferences of type String-Array
	 */
	public static class StringArrayPref extends Preference<String[]> {
		
		private final char fSeparator;
		
		public StringArrayPref(final String qualifier, final String key) {
			super(qualifier, key, Type.STRING);
			fSeparator = LIST_SEPARATOR_CHAR;
		}
		
		public StringArrayPref(final String qualifier, final String key, final char separator) {
			super(qualifier, key, Type.STRING);
			fSeparator = separator;
		}
		
		@Override
		public Class<String[]> getUsageType() {
			return String[].class;
		}
		@Override
		public String[] store2Usage(final Object storedValue) {
			final String s = (String) storedValue;
			if (s == null || s.length() == 0) {
				return new String[0];
			}
			switch (fSeparator) {
			case LIST_SEPARATOR_CHAR:
				return LIST_SEPARATOR_PATTERN.split(s);
			case IS2_SEPARATOR_CHAR:
				return IS2_SEPARATOR_PATTERN.split(s);
			}
			return ((fSeparator == LIST_SEPARATOR_CHAR) ?
					LIST_SEPARATOR_PATTERN : Pattern.compile("\\Q"+fSeparator+"\\E")).split(s);
		}
		@Override
		public String usage2Store(final String[] array) {
			if (array.length == 0) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb = new StringBuilder();
			for (final String s : array) {
				sb.append(s);
				sb.append(fSeparator);
			}
			return sb.substring(0, sb.length()-1);
		}
	}
	
	/**
	 * Default implementation for preferences of type String-Set
	 */
	public static class StringSetPref extends Preference<Set<String>> {
		
		public StringSetPref(final String qualifier, final String key) {
			super(qualifier, key, Type.STRING);
		}
		
		@Override
		public Class<Set<String>> getUsageType() {
			final Object o = Set.class;
			return (Class<Set<String>>) o;
		}
		@Override
		public Set<String> store2Usage(final Object storedValue) {
			final String s = (String) storedValue;
			if (s == null || s.length() == 0) {
				return new HashSet<String>(0);
			}
			final String[] strings = LIST_SEPARATOR_PATTERN.split(s);
			return new HashSet<String>(new ConstList<String>(strings));
		}
		@Override
		public String usage2Store(final Set<String> set) {
			if (set.size() == 0) {
				return ""; //$NON-NLS-1$
			}
			final StringBuilder sb = new StringBuilder();
			for (final String s : set) {
				sb.append(s);
				sb.append(',');
			}
			return sb.substring(0, sb.length()-1);
		}
	}
	
}
