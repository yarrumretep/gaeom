package com.google.code.gaeom.impl.encoder;

import com.google.appengine.api.datastore.Text;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TextEncoder extends AbstractSinglePropertyEncoder
{
	public TextEncoder(String property)
	{
		super(property, false);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		if (entityValue == null)
			return null;
		else
			return ((Text) entityValue).toString();
	}

	@Override
	protected Object encode(Object objectValue)
	{
		if (objectValue == null)
			return null;
		else
			return new Text(objectValue.toString());
	}
}
