package com.google.code.gaeom.impl.encoder;

import com.google.appengine.api.datastore.Blob;

public class ByteArrayEncoder extends AbstractSinglePropertyEncoder
{
	public ByteArrayEncoder(String property)
	{
		super(property, false);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		return ((Blob) entityValue).getBytes();
	}

	@Override
	protected Object encode(Object objectValue)
	{
		return new Blob((byte[]) objectValue);
	}
}
