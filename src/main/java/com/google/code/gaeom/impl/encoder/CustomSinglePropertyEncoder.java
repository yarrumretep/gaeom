package com.google.code.gaeom.impl.encoder;

import com.google.code.gaeom.CustomEncoder;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class CustomSinglePropertyEncoder extends AbstractSinglePropertyEncoder
{
	private final CustomEncoder<Object, Object> encoder;

	@SuppressWarnings("unchecked")
	public CustomSinglePropertyEncoder(String property, CustomEncoder<?, ?> encoder, boolean index)
	{
		super(property, index);
		this.encoder = (CustomEncoder<Object,Object>)encoder;
	}

	@Override
	protected Object decode(Object entityValue)
	{
		return encoder.decode(entityValue);
	}

	@Override
	protected Object encode(Object objectValue)
	{
		return encoder.encode(objectValue);
	}
}
