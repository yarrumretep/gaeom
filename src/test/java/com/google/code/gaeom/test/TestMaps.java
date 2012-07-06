package com.google.code.gaeom.test;

import java.util.Map;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestMaps extends AbstractLocalTest
{
	public static class Map1
	{
		Map<String, String> map = Maps.newHashMap();
	}
	
	@Test
	public void testMaps()
	{
		Map1 m = new Map1();
		m.map.put("foo", "bar");
		m.map.put("bas", "bat");
		
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(m).now();
		
		Map<String, String> orig = m.map;
		m.map = null;
		
		oss.refresh(m).now();
		
		assertEquals(orig, m.map);
	}
}
