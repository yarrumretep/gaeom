package com.google.code.gaeom.util;

import java.lang.reflect.Constructor;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectUtils
{
	@SuppressWarnings("unchecked")
	public static <T> T cast(Object o)
	{
		return (T) o;
	}

	public static <T> T newInstance(Class<T> clazz)
	{
		try
		{
			Constructor<T> constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		}
		catch (Exception e)
		{
			throw new IllegalStateException(e);
		}
	}

	public static void sleep(int i)
	{
		if (i > 0)
		{
			try
			{
				Thread.sleep(i);
			}
			catch (InterruptedException e)
			{
				throw new RuntimeException(e);
			}
		}
	}

	public static <T> Class<T> getWrapperClass(Class<T> c)
	{
		if (c.isPrimitive())
		{
			Class<?> clazz = null;

			if (c == boolean.class)
				clazz = Boolean.class;
			else if (c == byte.class)
				clazz = Byte.class;
			else if (c == char.class)
				clazz = Character.class;
			else if (c == short.class)
				clazz = Short.class;
			else if (c == int.class)
				clazz = Integer.class;
			else if (c == float.class)
				clazz = Float.class;
			else if (c == double.class)
				clazz = Double.class;
			else if (c == long.class)
				clazz = Long.class;
			else if (c == void.class)
				clazz = Void.class;
			return cast(clazz);
		}
		return c;
	}
}
