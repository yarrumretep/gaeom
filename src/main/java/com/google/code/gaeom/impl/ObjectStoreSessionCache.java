package com.google.code.gaeom.impl;

import java.util.Map;
import java.util.Set;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectStoreSessionCache
{
	final ObjectStoreCache upstream;
	final Map<Key, Object> keyToObject = Maps.newHashMap();
	final Map<Object, Key> objectToKey = Maps.newIdentityHashMap();
	final Set<Object> activatedObjects = Sets.newIdentityHashSet();

	public ObjectStoreSessionCache(ObjectStoreCache upstream)
	{
		this.upstream = upstream;
	}

	public Key getKey(Object object)
	{
		if (object == null)
			return null;

		Key key = objectToKey.get(object);
		if (key == null)
		{
			key = upstream.getKey(object);
			if (key != null)
				localSet(object, key);
		}
		return key;
	}

	@SuppressWarnings("unchecked")
	public <T> T getObject(Key key)
	{
		Object obj = keyToObject.get(key);
		if (obj == null)
		{
			obj = upstream.getObject(key);
			if (obj != null)
				localSet(obj, key);
		}
		return (T) obj;
	}

	public void set(Object object, Key key)
	{
		localSet(object, key);
		upstream.set(object, key);
	}

	private void localSet(Object object, Key key)
	{
		keyToObject.put(key, object);
		objectToKey.put(object, key);
	}

	public boolean isActivated(Object object)
	{
		return activatedObjects.contains(object);
	}

	public void activate(Object object)
	{
		activatedObjects.add(object);
	}

	public void clear()
	{
		keyToObject.clear();
		objectToKey.clear();
		activatedObjects.clear();
	}

	public void remove(Key key)
	{
		Object o = keyToObject.remove(key);
		objectToKey.remove(o);
		activatedObjects.remove(o);
		if (upstream != null)
			upstream.remove(key);
	}
}
