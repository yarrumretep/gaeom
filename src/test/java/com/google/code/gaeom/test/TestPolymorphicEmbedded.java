package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestPolymorphicEmbedded extends AbstractLocalTest
{
	public static class A
	{
		@Embedded
		List<B> bs = Lists.newArrayList();

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bs == null) ? 0 : bs.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			A other = (A) obj;
			if (bs == null)
			{
				if (other.bs != null)
					return false;
			}
			else if (!bs.equals(other.bs))
				return false;
			return true;
		}
	}
	
	public static class B
	{
		String x;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((x == null) ? 0 : x.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			B other = (B) obj;
			if (x == null)
			{
				if (other.x != null)
					return false;
			}
			else if (!x.equals(other.x))
				return false;
			return true;
		}
	}

	
	public static class B1 extends B
	{
		String y;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((y == null) ? 0 : y.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			B1 other = (B1) obj;
			if (y == null)
			{
				if (other.y != null)
					return false;
			}
			else if (!y.equals(other.y))
				return false;
			return true;
		}
	}
	
	public static class B2 extends B
	{
		String z;

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = super.hashCode();
			result = prime * result + ((z == null) ? 0 : z.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (!super.equals(obj))
				return false;
			if (getClass() != obj.getClass())
				return false;
			B2 other = (B2) obj;
			if (z == null)
			{
				if (other.z != null)
					return false;
			}
			else if (!z.equals(other.z))
				return false;
			return true;
		}
	}
	
	@Test
	public void testPolymorphicEmbedded() throws Exception
	{
		A a = new A();
		
		B b = new B();
		b.x = "Fred";
		a.bs.add(b);
		
		B1 b1 = new B1();
		b1.x = "Bob";
		b1.y = "Struthers";
		a.bs.add(b1);
		
		B2 b2 = new B2();
		b2.x = "Schmoo";
		b2.z = "Blah";
		a.bs.add(b2);
		
		ObjectStore os = ObjectStore.Factory.create();
		os.register(B1.class.getSimpleName(), B1.class);
		os.register(B2.class.getSimpleName(), B2.class);
		Key key = os.beginSession().store(a).now();
		
		A a2 = os.beginSession().load(key).now();
		assertEquals(a, a2);
		
		Entity e = DatastoreServiceFactory.getDatastoreService().get(key);
		assertEquals(Lists.newArrayList(null, "B1", "B2"), e.getProperty("bs.contents.__type__"));
		assertEquals(Lists.newArrayList("Fred", "Bob", "Schmoo"), e.getProperty("bs.contents.x"));
		assertEquals(Lists.newArrayList(null, "Struthers", null), e.getProperty("bs.contents.y"));
		assertEquals(Lists.newArrayList(null, null, "Blah"), e.getProperty("bs.contents.z"));
	}
}
