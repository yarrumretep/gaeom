package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestPolymorphicRelationships extends AbstractLocalTest
{
	public static class Foo
	{
		AbstractBar bar1;
		AbstractBar bar2;
	}
	
	public static abstract class AbstractBar
	{
	}
	
	public static class Bar1 extends AbstractBar
	{
	}
	
	public static class Bar2 extends AbstractBar
	{
	}
	
	@Test
	public void testPolymorphicSingleRelationship()
	{
		Foo foo = new Foo();
		foo.bar1 = new Bar1();
		foo.bar2 = new Bar2();
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		oss.store(foo).now();
		
		foo.bar1 = null;
		foo.bar2 = null;
		
		oss.refresh(foo).now();
		
		assertTrue(foo.bar1 instanceof Bar1);
		assertTrue(foo.bar2 instanceof Bar2);
	}
	
	public static class Foo2
	{
		List<AbstractBar> bars = Lists.newArrayList();
	}
	
	@Test
	public void testPolymorphicMutlipleRelationship()
	{
		Foo2 foo = new Foo2();
		foo.bars.add(new Bar1());
		foo.bars.add(new Bar2());
		foo.bars.add(new Bar1());
		foo.bars.add(new Bar2());
		
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		oss.store(foo).now();
		
		foo.bars = null;
		
		oss.refresh(foo).now();
		
		assertEquals(4, foo.bars.size());
		assertTrue(foo.bars.get(0) instanceof Bar1);
		assertTrue(foo.bars.get(1) instanceof Bar2);
		assertTrue(foo.bars.get(2) instanceof Bar1);
		assertTrue(foo.bars.get(3) instanceof Bar2);
	}
}
