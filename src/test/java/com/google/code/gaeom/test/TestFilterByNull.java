package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.Find;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;

public class TestFilterByNull extends AbstractLocalTest
{
	public static class A
	{
		B b;
	}

	public static class B
	{

	}

	public static class A2
	{
		@Embedded
		B b;
	}

	@Override
	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Test
	public void testByFilteringForNull() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A a1 = new A();
		A a2 = new A();
		a2.b = new B();

		oss.store(a1, a2).now();

		A a1a = oss.find(A.class).filter("b", null).single().now();
		assertEquals(a1, a1a);

		A a2a = oss.find(A.class).filter("b", Find.Op.NotEqualTo, null).single().now();
		assertEquals(a2, a2a);
	}

	@Test
	public void testByFilteringForNullEmbedded() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A2 a1 = new A2();
		A2 a2 = new A2();
		a2.b = new B();

		oss.store(a1, a2).now();

		A2 a1a = oss.find(A2.class).filter("b", null).single().now();
		assertEquals(a1, a1a);

//		A2 a2a = oss.find(A2.class).filter("b", Find.Op.NotEqualTo, null).single().now();
//		assertEquals(a2, a2a);
	}
}
