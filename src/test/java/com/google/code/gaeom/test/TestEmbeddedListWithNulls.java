package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestEmbeddedListWithNulls extends AbstractLocalTest
{
	public static class A
	{
		@Embedded
		List<B> bs = Lists.newArrayList();
	}
	
	public static class B
	{
		String name;
		
		@SuppressWarnings("unused")
		private B()
		{
			this(null);
		}
		
		public B(String name)
		{
			this.name = name;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
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
			if (name == null)
			{
				if (other.name != null)
					return false;
			}
			else if (!name.equals(other.name))
				return false;
			return true;
		}
	}
	
	@Test
	public void testEmbeddedListWithNulls()
	{
		A a = new A();
		
		a.bs.add(new B("Fred"));
		a.bs.add(null);
		a.bs.add(new B("Sally"));
		
		ObjectStore os = ObjectStore.Factory.create();
		Key key = os.beginSession().store(a).now();
		
		A a2 = os.beginSession().load(key).now();
		
		assertEquals(a.bs, a2.bs);
	}
}
