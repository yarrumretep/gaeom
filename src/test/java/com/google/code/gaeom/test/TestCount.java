package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.Find.Op;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

public class TestCount extends AbstractLocalTest
{
	public static class Foo
	{
		int value;

		public Foo(int value)
		{
			this.value = value;
		}
	}

	@Override
	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Test
	public void testCount()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		List<Foo> foos = Lists.newArrayList();
		for (int ct = 0; ct < 100; ct++)
			foos.add(new Foo(ct));
		oss.store(foos).now();

		assertEquals(100, oss.find(Foo.class).count().now().intValue());

		assertEquals(50, oss.find(Foo.class).filter("value", Op.LessThan, 50).count().now().intValue());
		assertEquals(51, oss.find(Foo.class).filter("value", Op.LessThanOrEqualTo, 50).count().now().intValue());
		assertEquals(1, oss.find(Foo.class).filter("value", Op.EqualTo, 50).count().now().intValue());
		assertEquals(99, oss.find(Foo.class).filter("value", Op.NotEqualTo, 50).count().now().intValue());
		assertEquals(24, oss.find(Foo.class).filter("value", Op.GreaterThan, 75).count().now().intValue());
		assertEquals(25, oss.find(Foo.class).filter("value", Op.GreaterThanOrEqualTo, 75).count().now().intValue());
	}
}
