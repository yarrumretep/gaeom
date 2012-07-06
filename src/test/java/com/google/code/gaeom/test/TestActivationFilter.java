package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

public class TestActivationFilter extends AbstractLocalTest
{
	public static class A
	{
		String name;
		B b;
		C c;
	}

	public static class B
	{
		String name;
		List<E> es = Lists.newArrayList();
	}

	public static class C
	{
		String name;
		List<F> fs = Lists.newArrayList();
	}

	public static class F
	{
		String name;
		G g;
	}

	public static class E
	{
		String name;
	}

	public static class G
	{
		String name;
	}
	
	ObjectStore os;
	Key key;

	@Before
	public void setupData()
	{
		os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A a = new A();
		a.name = "A";
		a.b = new B();
		a.c = new C();
		a.b.name = "B";
		a.c.name = "C";
		for (int ct = 0; ct < 5; ct++)
		{
			E f = new E();
			f.name = "E" + ct;
			a.b.es.add(f);
		}
		for (int ct = 0; ct < 10; ct++)
		{
			F f = new F();
			f.name = "F" + ct;
			f.g = new G();
			f.g.name = "G" + ct;
			a.c.fs.add(f);
		}
		key = oss.store(a).now();
	}

	@Test
	public void testActivationFilter1()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate(0).now();
		assertTrue(a.b.name == null);
		assertTrue(a.c.name == null);
	}

	@Test
	public void testActivationFilter2()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate("").now();
		assertTrue(a.b.name == null);
		assertTrue(a.c.name == null);
	}

	@Test
	public void testActivationFilter3()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate("*").now();
		assertEquals("B", a.b.name);
		assertEquals("C", a.c.name);
		assertEquals(null, a.b.es.get(0).name);
		assertEquals(null, a.c.fs.get(0).name);
	}

	@Test
	public void testActivationFilter4()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate("b.*").now();
		assertEquals("B", a.b.name);
		assertEquals(null, a.c.name);
		assertEquals("E0", a.b.es.get(0).name);
		assertEquals(0, a.c.fs.size());
	}
	
	@Test
	public void testActivationFilter5()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate("c.*").now();
		assertEquals(null, a.b.name);
		assertEquals("C", a.c.name);
		assertEquals(0, a.b.es.size());
		assertEquals("F0", a.c.fs.get(0).name);
		assertEquals(null, a.c.fs.get(0).g.name);
	}
	
	@Test
	public void testActivationFilter6()
	{
		ObjectStoreSession oss = os.beginSession();
		A a = oss.load(key).activate("c.**").now();
		assertEquals(null, a.b.name);
		assertEquals("C", a.c.name);
		assertEquals(0, a.b.es.size());
		assertEquals("F0", a.c.fs.get(0).name);
		assertEquals("G0", a.c.fs.get(0).g.name);
	}
}
