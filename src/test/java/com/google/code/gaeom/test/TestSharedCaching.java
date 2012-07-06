package com.google.code.gaeom.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Cached;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestSharedCaching extends AbstractLocalTest
{
	@Cached
	public static class TestCache
	{
		String name;	
	}
	
	@Test
	public void testSharedCaching()
	{
		TestCache tc = new TestCache();
		tc.name = "Fred";
		
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss1 = os.beginSession();
		
		Key key = oss1.store(tc).now();
		
		ObjectStoreSession oss2 = os.beginSession();

		TestCache tc2 = (TestCache)oss2.load(key).now();
		
		assertTrue("same instance", tc == tc2);
	}
}
