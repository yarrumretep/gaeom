package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestUnactivatedLoading extends AbstractLocalTest
{
	public static class Foo
	{
		String name;

		@SuppressWarnings("unused")
		private Foo()
		{
		}

		public Foo(String name)
		{
			this.name = name;
		}
	}

	@Test
	public void testUnactivatedLoad()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		Key key = oss.store(new Foo("Fred Flintstone")).now();

		ObjectStoreSession oss2 = os.beginSession();
		Foo foo = oss2.load(key).unactivated().now();
		assertTrue(foo.name == null);
		assertEquals(key, oss2.getKey(foo));
	}
}
