package com.google.code.gaeom.impl;

import java.util.concurrent.ConcurrentMap;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.annotation.Cached;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.common.collect.MapMaker;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectStoreCache
{
	final ConcurrentMap<Key, Entry> keyToObject = new MapMaker().makeMap();
	final ConcurrentMap<Object, Key> objectToKey = new MapMaker().weakKeys().makeMap();

	private static class Entry
	{
		final Object object;
		final long expiration;

		public Entry(Object object, long duration)
		{
			super();
			this.object = object;
			this.expiration = System.currentTimeMillis() + duration;
		}

		public boolean isExpired()
		{
			return System.currentTimeMillis() - expiration > 0;
		}
	}

	public Key getKey(Object object)
	{
		return objectToKey.get(object);
	}

	public <T> T getObject(Key key)
	{
		Entry entry = keyToObject.get(key);
		if (entry != null)
		{
			if (entry.isExpired())
			{
				keyToObject.remove(key);
				return null;
			}
			else
			{
				return ObjectUtils.<T> cast(entry.object);
			}
		}
		return null;
	}

	public void set(Object object, Key key)
	{
		Cached cached = object.getClass().getAnnotation(Cached.class);
		if (cached != null)
		{
			keyToObject.put(key, new Entry(object, cached.value()));
			objectToKey.put(object, key);
		}
	}

	public void clear()
	{
		keyToObject.clear();
		objectToKey.clear();
	}

	public void remove(Key key)
	{
		Entry entry = keyToObject.remove(key);
		if (entry != null)
			objectToKey.remove(entry.object);
	}
}
