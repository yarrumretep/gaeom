package com.google.code.gaeom.impl.encoder;

public class CharArrayEncoder extends AbstractSinglePropertyEncoder
{
	public CharArrayEncoder(String property)
	{
		super(property, false);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		return ((String) entityValue).toCharArray();
	}

	@Override
	protected Object encode(Object objectValue)
	{
		return new String((char[]) objectValue);
	}
}
