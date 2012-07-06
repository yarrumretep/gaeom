package com.google.code.gaeom.impl.encoder;

public class CharEncoder extends AbstractSinglePropertyEncoder
{
	public CharEncoder(String property, boolean index)
	{
		super(property, index);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		return (char) ((Number) entityValue).intValue();
	}

	@Override
	protected Object encode(Object objectValue)
	{
		return (int) ((Character) objectValue).charValue();
	}
}
