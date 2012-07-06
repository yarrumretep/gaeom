package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

public class TestLoaders extends AbstractLocalTest
{
	public static class X
	{
		String blah;
		X other;
	}

	@Test
	public void testLoadSingleId()
	{
		ObjectStore os = ObjectStore.Factory.create();

		os.beginSession().store(new X(), new X(), new X()).ids(1, 2, 3).now();

		assertEquals(X.class, os.beginSession().load(X.class).id(1).now().getClass());
	}

	@Test
	public void testLoadMultipleId()
	{
		ObjectStore os = ObjectStore.Factory.create();

		os.beginSession().store(new X(), new X(), new X()).ids(1, 2, 3).now();

		assertEquals(3, os.beginSession().load(X.class).ids(1, 2, 3).now().size());
	}

	@Test
	public void testLoadSingleStringId()
	{
		ObjectStore os = ObjectStore.Factory.create();

		os.beginSession().store(new X(), new X(), new X()).ids("a", "b", "c").now();

		assertEquals(X.class, os.beginSession().load(X.class).id("a").now().getClass());
	}

	@Test
	public void testLoadMultipleStringId()
	{
		ObjectStore os = ObjectStore.Factory.create();

		os.beginSession().store(new X(), new X(), new X()).ids("a", "b", "c").now();

		assertEquals(3, os.beginSession().load(X.class).ids("a", "b", "c").now().size());
	}

	@Test
	public void testLoadSingleKey()
	{
		ObjectStore os = ObjectStore.Factory.create();

		Key key = os.beginSession().store(new X()).now();

		assertEquals(X.class, os.beginSession().load(key).now().getClass());
	}

	@Test
	public void testLoadMultipleKeys()
	{
		ObjectStore os = ObjectStore.Factory.create();

		List<Key> keys = os.beginSession().store(new X(), new X(), new X()).now();

		assertEquals(3, Lists.newArrayList(os.beginSession().load(keys).now()).size());
	}

	@Test
	public void testRefreshSingleKey()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X x = new X();
		x.blah = "fred";
		Key key = oss.store(x).now();
		x.blah = "mary";
		assertEquals("mary", oss.load(key).<X> now().blah);
		assertEquals("fred", oss.load(key).refresh().<X> now().blah);
	}

	@Test
	public void testRefreshSingleKey2()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X x = new X();
		x.blah = "fred";
		Key key = oss.store(x).now();
		x.blah = "mary";
		assertEquals("mary", oss.load(key).<X> now().blah);
		assertEquals("fred", oss.refresh(x).now().blah);
	}

	@Test
	public void testRefreshMultipleKeys()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X x1 = new X();
		x1.blah = "fred1";
		X x2 = new X();
		x2.blah = "fred2";
		X x3 = new X();
		x3.blah = "fred3";
		List<Key> keys = oss.store(x1, x2, x3).now();
		x1.blah = null;
		x2.blah = null;
		x3.blah = null;

		// without refresh
		List<X> same = Lists.newArrayList(oss.load(keys).<X> now());
		assertNull(same.get(0).blah);
		assertNull(same.get(1).blah);
		assertNull(same.get(2).blah);

		List<X> list = Lists.newArrayList(oss.load(keys).refresh().<X> now());

		assertEquals(x1, list.get(0));
		assertEquals(x2, list.get(1));
		assertEquals(x3, list.get(2));

		assertEquals("fred1", list.get(0).blah);
		assertEquals("fred2", list.get(1).blah);
		assertEquals("fred3", list.get(2).blah);
	}

	@Test
	public void testRefreshMultipleKeys2()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X x1 = new X();
		x1.blah = "fred1";
		X x2 = new X();
		x2.blah = "fred2";
		X x3 = new X();
		x3.blah = "fred3";
		List<Key> keys = oss.store(x1, x2, x3).now();
		x1.blah = null;
		x2.blah = null;
		x3.blah = null;

		// without refresh
		List<X> same = Lists.newArrayList(oss.load(keys).<X> now());
		assertNull(same.get(0).blah);
		assertNull(same.get(1).blah);
		assertNull(same.get(2).blah);

		List<X> list = Lists.newArrayList(oss.refresh(same).now());

		assertEquals(x1, list.get(0));
		assertEquals(x2, list.get(1));
		assertEquals(x3, list.get(2));

		assertEquals("fred1", list.get(0).blah);
		assertEquals("fred2", list.get(1).blah);
		assertEquals("fred3", list.get(2).blah);
	}

	@Test
	public void testLoadSingleParentedStringId()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X parent = new X();
		oss.store(parent).now();
		oss.store(new X()).id("a").parent(parent).now();

		assertEquals(X.class, oss.load(X.class).id("a").parent(parent).now().getClass());
	}

	@Test
	public void testLoadMultipleParentedStringId()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		X parent = new X();
		oss.store(parent).now();
		oss.store(new X(), new X(), new X()).ids("a", "b", "c").parent(parent).now();

		assertEquals(3, oss.load(X.class).ids("a", "b", "c").parent(parent).now().size());
	}

	// TODO: test failure of parents to get key
	// TODO: think about if we should allow ids to not match the number of objects in store()
	// TODO: should we unify the code path between single and double for the loaders (maybe we already have?)

}
