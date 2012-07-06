package com.google.code.gaeom.impl.encoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.google.appengine.api.datastore.Blob;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class SerializableEncoder extends AbstractSinglePropertyEncoder
{
	public SerializableEncoder(String property)
	{
		super(property, false);
	}

	@Override
	protected Object decode(Object entityValue)
	{
		if (entityValue == null)
		{
			return null;
		}
		else
		{
			try
			{
				Blob blob = (Blob) entityValue;
				ByteArrayInputStream bais = new ByteArrayInputStream(blob.getBytes());
				ObjectInputStream ois = new ObjectInputStream(bais);
				return ois.readObject();
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}
	}

	@Override
	protected Object encode(Object objectValue)
	{
		if (objectValue == null)
		{
			return null;
		}
		else
		{
			try
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream(256);
				ObjectOutputStream stream = new ObjectOutputStream(baos);
				stream.writeObject(objectValue);
				stream.close();
				return new Blob(baos.toByteArray());
			}
			catch (Exception e)
			{
				throw new IllegalStateException(e);
			}
		}
	}
}
