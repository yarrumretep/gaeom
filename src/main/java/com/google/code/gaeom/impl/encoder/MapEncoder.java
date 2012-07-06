package com.google.code.gaeom.impl.encoder;

import java.util.HashMap;
import java.util.Map;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.code.gaeom.util.ObjectUtils;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class MapEncoder implements FieldEncoder
{
	public static final String cKeyPrefix = ".keys";
	public static final String cValuePrefix = ".values";

	private final String nullProperty;
	private final String keyPrefix;
	private final String valuePrefix;
	private final Class<? extends Map<?, ?>> clazz;
	private final FieldEncoder keyEncoder;
	private final FieldEncoder valueEncoder;

	public MapEncoder(String prefix, Class<?> clazz, FieldEncoder keyEncoder, FieldEncoder valueEncoder)
	{
		this.nullProperty = prefix + ".__null__";
		this.keyPrefix = prefix + cKeyPrefix;
		this.valuePrefix = prefix + cValuePrefix;
		if (clazz == Map.class)
			this.clazz = ObjectUtils.cast(HashMap.class);
		else
			this.clazz = ObjectUtils.cast(clazz);
		this.keyEncoder = keyEncoder;
		this.valueEncoder = valueEncoder;
	}

	public FieldEncoder getDefaultKeyEncoder()
	{
		return keyEncoder; 
	}
	
	public FieldEncoder getDefaultValueEncoder()
	{
		return valueEncoder; 
	}
	
	@Override
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		Boolean isNull = source.getProperty(nullProperty);
		if (isNull != null && isNull)
		{
			return null;
		}
		else
		{
			Map<Object, Object> map = ObjectUtils.cast(ObjectUtils.newInstance(clazz));
			ListPropertySource keylps = new ListPropertySource(source, keyPrefix);
			ListPropertySource valuelps = new ListPropertySource(source, valuePrefix);
			try
			{
				while (true)
				{
					Object key = keyEncoder.decode(instanceSource, keylps);
					Object value = valueEncoder.decode(instanceSource, valuelps);
					if (keylps.incr() && valuelps.incr())
						map.put(key, value);
					else
						break;
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				// ignore, means we're done
			}
			return map;
		}
	}

	@Override
	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		if (value == null)
		{
			store.setProperty(nullProperty, Boolean.TRUE, true);
		}
		else
		{
			Map<Object, Object> map = ObjectUtils.cast(value);
			ListPropertyStore keyListStore = new ListPropertyStore(store, keyPrefix);
			ListPropertyStore valueListStore = new ListPropertyStore(store, valuePrefix);
			for (Map.Entry<Object, Object> entry : map.entrySet())
			{
				keyEncoder.encode(instanceStore, entry.getKey(), keyListStore);
				valueEncoder.encode(instanceStore, entry.getValue(), valueListStore);
				keyListStore.incr();
				valueListStore.incr();
			}
		}
	}
}
