package com.google.code.gaeom.util;

import com.google.common.base.Function;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class FieldFunction<T> implements Function<Object, T>
{
	public static <T> FieldFunction<T> create(String field)
	{
		return new FieldFunction<T>(field);
	}
	
	private final String field;
	
	private FieldFunction(String field)
	{
		this.field = field;
	}
	
	@Override
	public T apply(Object input)
	{
		return ReflectionUtils.<T>get(input, field);
	}
}
