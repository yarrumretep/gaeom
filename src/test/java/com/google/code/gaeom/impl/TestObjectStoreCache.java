package com.google.code.gaeom.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.code.gaeom.annotation.Cached;
import com.google.code.gaeom.test.AbstractLocalTest;
import com.google.code.gaeom.util.ObjectUtils;

public class TestObjectStoreCache extends AbstractLocalTest
{
	@Cached(200)
	public static class Foo
	{
	}

	@Test
	public void testNoCache()
	{
		ObjectStoreCache cache = new ObjectStoreCache();

		Key key = KeyFactory.createKey("fred", 12);
		Object object = new Object();

		cache.set(object, key);

		assertTrue(cache.getObject(key) == null);
		assertTrue(cache.getKey(object) == null);
	}

	@Test
	public void testExpiration()
	{
		ObjectStoreCache cache = new ObjectStoreCache();

		Key key = KeyFactory.createKey("fred", 12);
		Foo object = new Foo();

		cache.set(object, key);

		assertEquals(object, cache.getObject(key));
		assertEquals(key, cache.getKey(object));

		ObjectUtils.sleep(400);

		assertEquals(null, cache.getObject(key));
		assertEquals(key, cache.getKey(object));
	}

	@Test
	public void testClearing()
	{
		ObjectStoreCache cache = new ObjectStoreCache();

		Key key = KeyFactory.createKey("fred", 12);
		Foo object = new Foo();

		cache.set(object, key);

		assertEquals(object, cache.getObject(key));
		assertEquals(key, cache.getKey(object));
		
		cache.clear();

		assertEquals(null, cache.getObject(key));
		assertEquals(null, cache.getKey(object));
	}
}
