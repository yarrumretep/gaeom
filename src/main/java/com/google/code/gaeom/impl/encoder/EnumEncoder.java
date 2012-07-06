package com.google.code.gaeom.impl.encoder;

import com.google.code.gaeom.util.ObjectUtils;
import com.google.code.gaeom.util.ReflectionUtils;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class EnumEncoder extends AbstractSinglePropertyEncoder
{
	final Class<? extends Enum<?>> clazz;

	public EnumEncoder(String property, Class<?> clazz, boolean index)
	{
		super(property, index);
		this.clazz = ObjectUtils.cast(clazz);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		if (entityValue == null)
			return null;
		else
			return ReflectionUtils.enumValueOf(clazz, (String) entityValue);
	}

	@Override
	protected Object encode(Object objectValue)
	{
		if (objectValue == null)
			return null;
		else
			return ((Enum<?>) objectValue).name();
	}
}
