package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.Find.Op;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Parent;
import com.google.code.gaeom.annotation.Parent.FilterPolicy;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestParentQueries extends AbstractLocalTest
{
	public static class A
	{
	}

	public static class B1
	{
		@Parent(FilterPolicy.AncestorQuery)
		A a;
	}

	public static class B2
	{
		@Parent(FilterPolicy.NoFilter)
		A a;
	}

	public static class B3
	{
		@Parent
		A a;
	}

	protected boolean simulateEventualConsistency()
	{
		return false;
	}
	
	@Test
	public void testAncestorQuery()
	{
		A a = new A();

		B1 b = new B1();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		B1 b1 = oss.find(B1.class).filter("a", a).single().now();
		assertEquals(b, b1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidAncestorQuery()
	{
		A a = new A();

		B1 b = new B1();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		B1 b1 = oss.find(B1.class).filter("a", Op.GreaterThan, a).single().now();
		assertEquals(b, b1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidAncestorQuery2()
	{
		A a = new A();

		B3 b = new B3();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		os.beginSession().find(B3.class).filter("a", a).single().now();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNoFilterQuery()
	{
		A a = new A();

		B2 b = new B2();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		oss.find(B2.class).filter("a", a).single().now();
	}

	@Test
	public void testRegularQuery()
	{
		A a = new A();

		B3 b = new B3();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		B3 b1 = oss.find(B3.class).filter("a", a).single().now();
		assertEquals(b, b1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidField()
	{
		A a = new A();

		B1 b = new B1();
		b.a = a;

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a, b).now();

		oss.find(B1.class).filter("blahblah", a).single().now();
	}
}
