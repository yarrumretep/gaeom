package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Ignore;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;

public class TestActivationTracking extends AbstractLocalTest
{
	public static class A
	{
		A b;
		String name;
	}

	ObjectStore os = ObjectStore.Factory.create();

	private Key load()
	{
		A a = new A();
		a.name = "Sally";
		a.b = new A();
		a.b.name = "Fred";

		ObjectStoreSession oss = os.beginSession();

		Key key = oss.store(a).now();
		assertTrue(oss.isActivated(a));
		assertTrue(oss.isActivated(a.b));
		return key;
	}

	@Test
	public void testActivationTracking()
	{
		Key key = load();

		ObjectStoreSession oss2 = os.beginSession();
		A a2 = oss2.load(key).activate(0).now();
		assertTrue(oss2.isActivated(a2));
		assertFalse(oss2.isActivated(a2.b));
		assertNull(a2.b.name);
		oss2.refresh(a2.b).now();
		assertTrue(oss2.isActivated(a2.b));
		assertEquals("Fred", a2.b.name);

		ObjectStoreSession oss3 = os.beginSession();
		A a3 = oss3.load(key).now();
		assertTrue(oss3.isActivated(a3));
		assertTrue(oss3.isActivated(a3.b));
		assertEquals("Fred", a3.b.name);
	}

	@Test
	public void testExplicitActivationOfPreLoadedUnactivatedObjects()
	{
		load();
		ObjectStoreSession oss2 = os.beginSession();
		Iterable<A> as = oss2.find(A.class).activate(0).now();

		for (A a : as)
			assertTrue(oss2.isActivated(a));
	}

	@Ignore
	@Test
	public void testExplicitActivationOfPreLoadedUnactivatedObjects2()
	{
		A a = new A();
		a.name = "Sally";
		a.b = new A();
		a.b.name = "Fred";
		a.b.b = new A();
		a.b.b.name = "Martha";

		ObjectStoreSession oss = os.beginSession();

		Key key = oss.store(a).now();
		assertTrue(oss.isActivated(a));
		assertTrue(oss.isActivated(a.b));
		Key key1 = oss.getKey(a.b);

		ObjectStoreSession oss2 = os.beginSession();
		oss2.load(key1).activate(0).now();  //load a.b activated, but a.b.b unactivated
		
		A a1 = oss2.load(key).now(); // load the whole graph
		assertTrue(oss2.isActivated(a1));
		assertTrue(oss2.isActivated(a1.b));
		assertTrue(oss2.isActivated(a1.b.b));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testDetectStoreUnactivated()
	{
		Key key = load();
		ObjectStoreSession oss2 = os.beginSession();
		A a = oss2.load(key).activate(0).now();

		a.b.name = "bob";
		oss2.store(a.b).now();
	}

}
