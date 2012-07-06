package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestCollectionEncoding extends AbstractLocalTest
{
	public static class Foo
	{
		List<String> values = Lists.newArrayList();
	}
	
	@Test
	public void testSimpleList()
	{
		Foo foo= new Foo();
		foo.values.add("Foo");
		foo.values.add("Bar");
		foo.values.add("Bas");
		
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(foo).now();
		
		List<String> orig = foo.values;
		foo.values = null;
		
		oss.refresh(foo).now();
		
		assertEquals(orig, foo.values);
	}
	
	public static class Person
	{
		@Embedded
		List<Phone> phones = Lists.newArrayList();
	}
	
	public static class Phone
	{
		Integer areaCode;
		Integer prefix;
		Integer suffix;
		
		public Phone()
		{
		}
		
		public Phone(int areaCode, int prefix, int suffix)
		{
			this.areaCode = areaCode;
			this.prefix = prefix;
			this.suffix = suffix;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((areaCode == null) ? 0 : areaCode.hashCode());
			result = prime * result + ((prefix == null) ? 0 : prefix.hashCode());
			result = prime * result + ((suffix == null) ? 0 : suffix.hashCode());
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
			Phone other = (Phone) obj;
			if (areaCode == null)
			{
				if (other.areaCode != null)
					return false;
			}
			else if (!areaCode.equals(other.areaCode))
				return false;
			if (prefix == null)
			{
				if (other.prefix != null)
					return false;
			}
			else if (!prefix.equals(other.prefix))
				return false;
			if (suffix == null)
			{
				if (other.suffix != null)
					return false;
			}
			else if (!suffix.equals(other.suffix))
				return false;
			return true;
		}

		@Override
		public String toString()
		{
			return "Phone [" + areaCode + "-" + prefix + "-" + suffix + "]";
		}
	}
	
	@Test
	public void testEmbeddeObjectList()
	{
		Person p = new Person();
		p.phones.add(new Phone(207, 444, 3333));
		p.phones.add(new Phone(207, 686, 7777));
		p.phones.add(new Phone(207, 555, 7777));
		p.phones.get(1).prefix = null;  // prove out the rectangulation of the thing
		
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(p).now();
		
		List<Phone> orig = p.phones;
		p.phones = null;
		
		oss.refresh(p).now();
		
		assertEquals(orig, p.phones);
	}
}
