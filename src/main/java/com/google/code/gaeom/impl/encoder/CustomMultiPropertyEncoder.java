package com.google.code.gaeom.impl.encoder;

import java.util.Map;

import com.google.code.gaeom.CustomMultiEncoder;
import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class CustomMultiPropertyEncoder implements FieldEncoder
{
	private final String prefix;
	private final CustomMultiEncoder<Object> encoder;
	private final boolean index;

	@SuppressWarnings("unchecked")
	public CustomMultiPropertyEncoder(String prefix, CustomMultiEncoder<?> encoder, boolean index)
	{
		this.prefix = prefix + ".";
		this.encoder = (CustomMultiEncoder<Object>) encoder;
		this.index = index;
	}

	@Override
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		Map<String, Object> properties = Maps.newHashMap();
		for (String key : source.getKeys())
		{
			if (key.startsWith(prefix))
				properties.put(key.substring(prefix.length()), source.getProperty(key));
		}
		return encoder.decode(properties);
	}

	@Override
	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		Map<String, ?> properties = encoder.encode(value);
		for (Map.Entry<String, ?> entry : properties.entrySet())
			store.setProperty(prefix + entry.getKey(), entry.getValue(), index);
	}
}
