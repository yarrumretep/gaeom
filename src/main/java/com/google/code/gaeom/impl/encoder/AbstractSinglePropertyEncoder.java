package com.google.code.gaeom.impl.encoder;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public abstract class AbstractSinglePropertyEncoder implements FieldEncoder
{
	private final String property;
	private final boolean index;

	protected AbstractSinglePropertyEncoder(String property, boolean index)
	{
		this.property = property;
		this.index = index;
	}

	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		return decode(source.getProperty(property));
	}

	protected abstract Object decode(Object entityValue);

	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		store.setProperty(property, encode(value), index);
	}

	protected abstract Object encode(Object objectValue);
}
