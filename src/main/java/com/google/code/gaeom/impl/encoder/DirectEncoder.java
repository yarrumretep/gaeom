package com.google.code.gaeom.impl.encoder;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class DirectEncoder extends AbstractSinglePropertyEncoder
{
	public DirectEncoder(String property, boolean index)
	{
		super(property, index);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		return entityValue;
	}

	@Override
	protected Object encode(Object objectValue)
	{
		return objectValue;
	}
}
