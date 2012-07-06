package com.google.code.gaeom.test;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.KeysNotFoundException;
import com.google.code.gaeom.ObjectStore;

public class TestNotFoundException extends AbstractLocalTest
{
	public static class Blah
	{

	}

	@Override
	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Test
	public void testNotFoundException()
	{
		try
		{
			ObjectStore.Factory.create().beginSession().store(new Blah()).id(2).now();
			ObjectStore.Factory.create().beginSession().load(Blah.class).ids(1, 2, 3).retries(0).now();
			assertTrue(false);
		}
		catch (KeysNotFoundException e)
		{
			assertEquals(2, e.getKeys().size());
			Iterator<Key> iter = e.getKeys().iterator();
			assertEquals(1, iter.next().getId());
			assertEquals(3, iter.next().getId());
		}
	}
}
