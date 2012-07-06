package com.google.code.gaeom.impl.encoder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.code.gaeom.util.ObjectUtils;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class CollectionEncoder implements FieldEncoder
{
	public static final String cContentsPrefix = ".contents";

	final String nullProperty;
	final String prefix;
	final Class<? extends Collection<?>> clazz;
	final FieldEncoder encoder;

	@SuppressWarnings("unchecked")
	public CollectionEncoder(String prefix, Class<?> clazz, FieldEncoder encoder)
	{
		this.nullProperty = prefix + ".__null__";
		this.prefix = prefix + cContentsPrefix;
		if (clazz == List.class)
			this.clazz = ObjectUtils.cast(ArrayList.class);
		else if (clazz == Set.class || clazz == Collection.class)
			this.clazz = ObjectUtils.cast(HashSet.class);
		else
			this.clazz = (Class<? extends Collection<?>>) clazz;
		this.encoder = encoder;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object decode(final InstanceSource instanceSource, final PropertySource source)
	{
		Boolean isNull = source.getProperty(nullProperty);
		if (isNull != null && isNull)
		{
			return null;
		}
		else
		{
			Collection<Object> collection = (Collection<Object>) ObjectUtils.newInstance(clazz);
			ListPropertySource lps = new ListPropertySource(source, prefix);
			try
			{
				while (true)
				{
					Object o = encoder.decode(instanceSource, lps);
					if (lps.incr())
						collection.add(o);
					else
						break;
				}
			}
			catch (IndexOutOfBoundsException e)
			{
				// ignore, means we're done
			}
			return collection;
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
			Collection<?> collection = (Collection<?>) value;
			ListPropertyStore listStore = new ListPropertyStore(store, prefix);
			for (Object member : collection)
			{
				encoder.encode(instanceStore, member, listStore);
				listStore.incr();
			}
		}
	}

	public FieldEncoder getDefaultEncoder()
	{
		return encoder;
	}
}
