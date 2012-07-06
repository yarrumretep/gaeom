package com.google.code.gaeom.test;

import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.KeysNotFoundException;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestDelete extends AbstractLocalTest
{
	public static class Tester
	{
		String name;
	}

	@Test(expected = KeysNotFoundException.class)
	public void testDeleteCommand()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Tester t1 = new Tester();
		t1.name = "Fred Flintstone";
		Key key = oss.store(t1).now();

		oss.delete(t1).now();

		os.beginSession().load(key).now(); // may not fail because changes not rolled forward see superclass

		assertNull(os.beginSession().load(key).retries(0).now());
	}

	@Test(expected = KeysNotFoundException.class)
	public void testDeleteCommandLocal()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Tester t1 = new Tester();
		t1.name = "Fred Flintstone";
		Key key = oss.store(t1).now();

		oss.delete(t1).now();

		os.beginSession().load(key).now(); // may not fail because changes not rolled forward see superclass

		assertNull(oss.load(key).retries(0).now());
	}
}
