package com.google.code.gaeom;

import java.util.List;

import com.google.appengine.api.datastore.Key;
import com.google.common.collect.Lists;

/**
 * Runtime exception thrown when the number of retries for resolving EVENTUAL consistency issues is exceeded. It will
 * contain a list of keys that could not be resolved.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class KeysNotFoundException extends RuntimeException
{
	private static final long serialVersionUID = -5652138864822725800L;

	private final List<Key> keys;

	public KeysNotFoundException(Iterable<Key> keys)
	{
		super("Keys not found: " + keys.toString());
		this.keys = Lists.newArrayList(keys);
	}

	public List<Key> getKeys()
	{
		return keys;
	}
}
