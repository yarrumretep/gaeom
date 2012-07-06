package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestStoring extends AbstractLocalTest
{
	public static class Foo
	{
		String name;
		
		@SuppressWarnings("unused")
		private Foo()
		{
		}
		
		public Foo(String name)
		{
			this.name = name;
		}
	}
	
	@Test
	public void testReStoring()
	{
		Foo foo = new Foo("Fred Flintstone");
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		oss.store(foo).now();
		
		foo.name = "Barney Rubble";
		
		oss.store(foo).now();
		
		List<Foo> foos = Lists.newArrayList(oss.find(Foo.class).now());
		assertEquals(1, foos.size());
		assertEquals(foo, foos.get(0));
	}
}
