package com.google.code.gaeom.test;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.KeysNotFoundException;
import com.google.code.gaeom.ObjectStore;

public class TestEvenutalConsistency extends AbstractLocalTest
{
	public static class Foo
	{
		String blah;
	}

	@Test
	public void testEvenutalConsistency() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		Key key = os.beginSession().store(new Foo()).now();
		
		try
		{
			os.beginSession().load(key).retries(0).now();
			assertTrue(false); // should have thrown
		}
		catch (KeysNotFoundException e)
		{
		}
		
		assertTrue(os.beginSession().load(key).retries(0).now() != null);
	}
}
