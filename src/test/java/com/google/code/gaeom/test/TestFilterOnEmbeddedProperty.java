package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

public class TestFilterOnEmbeddedProperty extends AbstractLocalTest
{
	public static class A
	{
		String name;
		@Embedded
		B b;
		@Embedded
		List<B> bs = Lists.newArrayList();
		
		List<String> strings = Lists.newArrayList();
	}

	public static class B
	{
		String foo;
	}

	@Override
	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Test
	public void queryOnEmbeddedProperty() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A a = new A();
		a.name = "Fred";
		a.b = new B();
		a.b.foo = "Blah";

		oss.store(a).now();

		A a2 = oss.find(A.class).filter("b.foo", "Blah").single().now();
		assertTrue(a == a2);
	}

	@Test
	public void queryOnEmbeddedListProperty() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A a = new A();
		a.name = "Fred";
		a.bs.add(new B());
		a.bs.add(new B());
		a.bs.add(new B());
		a.bs.get(0).foo = "Blah";
		a.bs.get(1).foo = "Blah1";
		a.bs.get(2).foo = "Blah2";

		oss.store(a).now();

		A a2 = oss.find(A.class).filter("bs.foo", "Blah").single().now();
		assertTrue(a == a2);
	}
	
	@Test
	public void queryOnEmbeddedListStringProperty() throws Exception
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		A a = new A();
		a.name = "Fred";
		a.strings.add("Blah");
		a.strings.add("Blah2");
		a.strings.add("Blah3");

		oss.store(a).now();

		A a2 = oss.find(A.class).filter("strings", "Blah").single().now();
		assertTrue(a == a2);
	}

}
