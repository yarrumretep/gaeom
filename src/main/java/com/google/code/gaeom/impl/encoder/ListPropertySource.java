package com.google.code.gaeom.impl.encoder;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
class ListPropertySource implements PropertySource
{
	private final String prefix;
	private final PropertySource source;
	private int offset = 0;
	private Set<String> keys = null;
	private boolean foundData;

	ListPropertySource(PropertySource source, String prefix)
	{
		this.source = source;
		this.prefix = prefix;
		foundData = false;
	}

	@Override
	public Collection<String> getKeys()
	{
		if (keys == null)
		{
			keys = Sets.newHashSet();
			for (String string : source.getKeys())
			{
				if (string.startsWith(prefix))
					keys.add(string);
			}
		}
		return keys;
	}

	@Override
	public <T> T getProperty(String key)
	{
		List<?> list = (List<?>) source.getProperty(key);
		if(list == null)
			return null;
		else
		{
			foundData = true;
			return ObjectUtils.<T>cast(list.get(offset));
		}
	}

	public boolean incr()
	{
		offset++;
		boolean foundDataLastRound = foundData;
		foundData = false;
		return foundDataLastRound;
	}
}