package com.google.code.gaeom.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ReflectionUtils
{
	public static interface FieldCallback
	{
		/**
		 * @param field
		 * @return true to continue through fields
		 */
		boolean doWith(Field field);
	}

	public static Field findField(Class<?> clazz, String field)
	{
		if (clazz == null)
			return null;

		try
		{
			return clazz.getDeclaredField(field);
		}
		catch (NoSuchFieldException e)
		{
			return findField(clazz.getSuperclass(), field);
		}
	}

	public static void visitFields(Class<?> clazz, FieldCallback callback)
	{
		_visitFields(clazz, callback);
	}

	private static boolean _visitFields(Class<?> clazz, FieldCallback callback)
	{
		if (clazz != null)
		{
			if (!_visitFields(clazz.getSuperclass(), callback))
				return false;

			for (Field field : clazz.getDeclaredFields())
			{
				if (!callback.doWith(field))
					return false;
			}
		}
		return true;
	}

	private static class FieldEqualVisitor<T> implements FieldCallback
	{
		final T o1;
		final T o2;
		boolean equal = true;

		public FieldEqualVisitor(T o1, T o2)
		{
			super();
			this.o1 = o1;
			this.o2 = o2;
		}

		public boolean doWith(Field field)
		{
			field.setAccessible(true);
			try
			{
				equal = fieldsEqual(field.get(o1), field.get(o2));
				return equal;
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}
	}

	public static <T> boolean fieldsEqual(final T o1, final T o2)
	{
		return fieldsEqual(o1, o2, Sets.<Pair<Object, Object>> newIdentityHashSet());
	}

	private static boolean implementsEquals(Class<?> clazz)
	{
		try
		{
			Method m = clazz.getMethod("equals", Object.class);
			return m.getDeclaringClass() != Object.class;
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	public static <T> boolean fieldsEqual(final T o1, final T o2, final Set<Pair<Object, Object>> seen)
	{
		if (o1 == null)
			return o2 == null;
		else if (o2 == null)
			return false;

		Pair<Object, Object> pair = new Pair<Object, Object>(o1, o2);
		if (!seen.add(pair))
			return true;
		try
		{
			if (o1 instanceof Collection<?>)
			{
				Collection<Object> c1 = ObjectUtils.cast(o1);
				Collection<Object> c2 = ObjectUtils.cast(o2);
				if (c1.size() != c2.size())
					return false;
				Iterator<Object> i1 = c1.iterator();
				Iterator<Object> i2 = c2.iterator();
				while (i1.hasNext())
				{
					if (!fieldsEqual(i1.next(), i2.next()))
						return false;
				}
				return true;
			}
			else if (o1 instanceof Map<?, ?>)
			{
				Map<Object, Object> m1 = ObjectUtils.cast(o1);
				Map<Object, Object> m2 = ObjectUtils.cast(o2);
				for (Map.Entry<Object, Object> entry : m1.entrySet())
				{
					if (!fieldsEqual(entry.getValue(), m2.get(entry.getKey())))
						return false;
				}
				return true;
			}
			else if (implementsEquals(o1.getClass()))
			{
				return o1.equals(o2);
			}
			else
			{
				FieldEqualVisitor<T> visitor = new FieldEqualVisitor<T>(o1, o2);
				visitFields(o1.getClass(), visitor);
				return visitor.equal;
			}
		}
		finally
		{
			seen.remove(pair);
		}
	}

	public static Class<?> getBaseClass(Type type)
	{
		if (type instanceof Class<?>)
			return (Class<?>) type;
		else if (type instanceof ParameterizedType)
			return getBaseClass(((ParameterizedType) type).getRawType());
		else
			throw new IllegalArgumentException("Don't know how to handle type: " + type);
	}

	public static void set(Object target, String name, Object value)
	{
		set(target, findField(target.getClass(), name), value);
	}

	public static void set(Object target, Field field, Object value)
	{
		try
		{
			field.set(target, value);
		}
		catch (Exception ex)
		{
			throw new IllegalStateException(ex);
		}
	}

	public static <T> T get(Object target, String name)
	{
		return ReflectionUtils.<T> get(target, findField(target.getClass(), name));
	}

	public static <T> T get(Object target, Field field)
	{
		try
		{
			return ObjectUtils.<T> cast(field.get(target));
		}
		catch (Exception ex)
		{
			throw new IllegalStateException(ex);
		}
	}

	public static <T> T invokeStatic(Class<?> clazz, String methodName, Object... args)
	{
		Class<?>[] parameterTypes = new Class<?>[args.length];
		for (int ct = 0; ct < args.length; ct++)
			parameterTypes[ct] = args[ct].getClass();
		try
		{
			Method method = clazz.getMethod(methodName, parameterTypes);
			return ObjectUtils.<T> cast(method.invoke(null, args));
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static Enum<?> enumValueOf(Class<? extends Enum<?>> clazz, String name)
	{
		return invokeStatic(clazz, "valueOf", name);
	}
}
