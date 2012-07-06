package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.Find.Op;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestFilterWithEmbeddedObject extends AbstractLocalTest
{
	public static class A
	{
		@Embedded
		Point p;
	}

	public static class Point
	{
		final int x;
		final int y;

		@SuppressWarnings("unused")
		private Point()
		{
			this(0, 0);
		}

		public Point(int x, int y)
		{
			this.x = x;
			this.y = y;
		}
	}

	protected boolean simulateEventualConsistency()
	{
		return false;
	}
	
	@Test
	public void testEmbeddedQuerying()
	{
		A a = new A();
		a.p = new Point(10, 20);

		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(a).now();

		A a1 = oss.find(A.class).filter("p", new Point(10, 20)).single().now();

		assertEquals(a, a1);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testFailedEmbeddedQuerying()
	{
		A a = new A();
		a.p = new Point(10, 20);

		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(a).now();

		oss.find(A.class).filter("p", Op.GreaterThan, new Point(10, 20)).single().now();
	}
}
