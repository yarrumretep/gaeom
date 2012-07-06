package com.google.code.gaeom.impl.encoder;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.google.code.gaeom.impl.PropertyStore;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
class ListPropertyStore implements PropertyStore
{
	final String prefix;
	final PropertyStore store;
	final Map<String, List<Object>> lists = Maps.newHashMap();
	int count = 0;

	ListPropertyStore(PropertyStore store, String prefix)
	{
		this.store = store;
		this.prefix = prefix;
	}

	@Override
	public void setProperty(String key, Object value, boolean index)
	{
		assert key.startsWith(prefix);
		
		if(value instanceof Collection || value instanceof Map)
			throw new IllegalArgumentException("Nested embedded collections are not supported.");
		
		List<Object> list = lists.get(key);
		if (list == null)
		{
			list = Lists.newArrayList();
			pad(list);
			store.setProperty(key, list, index);
			lists.put(key, list);
		}
		list.add(value);
	}

	private void pad(List<Object> list)
	{
		while (list.size() < count)
			list.add(null);
	}

	public void incr()
	{
		count++;
		for (List<Object> list : lists.values())
			pad(list);
	}
}