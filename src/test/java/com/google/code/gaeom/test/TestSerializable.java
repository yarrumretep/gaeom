package com.google.code.gaeom.test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Serialize;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestSerializable extends AbstractLocalTest
{
	public static class Foo
	{
		@Serialize
		Map<String, Object> map = Maps.newHashMap();
	}
	
	@Test
	public void testSerializable()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		Foo foo = new Foo();
		
		foo.map.put("abc", "def");
		foo.map.put("xyz", 23L);
		
		Map<String, Object> backup = foo.map;
		
		oss.store(foo).now();
		
		foo.map = null;
		
		oss.refresh(foo).now();
		
		assertEquals(backup, foo.map);
	}
}
