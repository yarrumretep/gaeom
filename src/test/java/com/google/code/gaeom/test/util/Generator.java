package com.google.code.gaeom.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.google.code.gaeom.util.ObjectUtils;
import com.google.code.gaeom.util.ReflectionUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class Generator<T>
{
	public static <T> Generator<T> generate(Class<T> clazz)
	{
		return new Generator<T>(clazz);
	}

	private final Class<T> clazz;
	private Integer count = null;
	private int levels = 10;

	private Generator(Class<T> clazz)
	{
		this.clazz = clazz;
	}

	public Generator<List<T>> count(int count)
	{
		this.count = count;
		return ObjectUtils.cast(this);
	}

	public Generator<T> levels(int levels)
	{
		this.levels = levels;
		return this;
	}

	private <X> X create(Class<X> clazz)
	{
		return ObjectUtils.<X> cast(create(clazz, levels));
	}

	private Object create(final Type type, final int remainingLevels)
	{
		Class<?> clazz = ReflectionUtils.getBaseClass(type);

		if (remainingLevels == 0)
			return null;

		final Object value;
		if (List.class.isAssignableFrom(clazz))
		{
			if (List.class == clazz)
				value = Lists.newArrayList();
			else
				value = ObjectUtils.newInstance(clazz);
			List<Object> list = ObjectUtils.cast(value);
			Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			for (int ct = 0; ct < RandomUtils.nextInt(5) + 2; ct++)
				list.add(create(types[0], remainingLevels));
		}
		else if (Map.class.isAssignableFrom(clazz))
		{
			if (Map.class == clazz)
				value = Maps.newHashMap();
			else
				value = ObjectUtils.newInstance(clazz);
			Map<Object, Object> map = ObjectUtils.cast(value);
			Type[] types = ((ParameterizedType) type).getActualTypeArguments();
			for (int ct = 0; ct < RandomUtils.nextInt(5) + 2; ct++)
				map.put(create(types[0], remainingLevels), create(types[1], remainingLevels));
		}
		else if (String.class == clazz)
		{
			value = RandomStringUtils.randomAlphanumeric(15);
		}
		else if (Long.class == clazz || long.class == clazz)
		{
			value = RandomUtils.nextLong();
		}
		else if (Integer.class == clazz || int.class == clazz)
		{
			value = RandomUtils.nextInt(100);
		}
		else if (Double.class == clazz || double.class == clazz)
		{
			value = RandomUtils.nextDouble();
		}
		else if (Float.class == clazz || float.class == clazz)
		{
			value = RandomUtils.nextFloat();
		}
		else if (Character.class == clazz || char.class == clazz)
		{
			value = RandomStringUtils.randomAlphabetic(1).charAt(0);
		}
		else if (Byte.class == clazz || byte.class == clazz)
		{
			value = (byte) RandomUtils.nextInt(256);
		}
		else if (Boolean.class == clazz || boolean.class == clazz)
		{
			value = RandomUtils.nextBoolean();
		}
		else if (Date.class == clazz)
		{
			value = new Date(System.currentTimeMillis() - 365 * 24 * 60 * 60 * 1000 + RandomUtils.nextInt(2 * 365 * 24 * 60 * 60 * 1000));
		}
		else
		{
			value = ObjectUtils.newInstance(clazz);
			if (remainingLevels > 1)
			{
				ReflectionUtils.visitFields(clazz, new ReflectionUtils.FieldCallback()
				{
					@Override
					public boolean doWith(Field field)
					{
						if (!Modifier.isStatic(field.getModifiers()))
						{
							field.setAccessible(true);
							ReflectionUtils.set(value, field, create(field.getGenericType(), remainingLevels - 1));
						}
						return true;
					}
				});
			}
		}
		return value;
	}

	public T generate()
	{
		if (count != null)
		{
			List<Object> list = Lists.newArrayList();
			for (int ct = 0; ct < count; ct++)
				list.add(create(clazz));
			return ObjectUtils.<T> cast(list);
		}
		else
		{
			return ObjectUtils.<T> cast(create(clazz));
		}
	}
}
