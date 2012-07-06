package com.google.code.gaeom.test;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestNulls extends AbstractLocalTest
{
	public static class A
	{
		List<String> list;
		Map<String, Long> map;
	}

	protected boolean simulateEventualConsistency()
	{
		return false;
	}
	
	@Test
	public void testNullHandling()
	{
		A a = new A();

		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(a).now();

		a.list = Collections.emptyList();
		a.map = Collections.emptyMap();

		oss.refresh(a).now();
		assertTrue(a.list == null);
		assertTrue(a.map == null);

		a.list = Lists.newArrayList();
		a.map = Maps.newHashMap();

		oss.store(a).now();

		a.list = null;
		a.map = null;

		oss.refresh(a).now();

		assertEquals(0, a.list.size());
		assertEquals(0, a.map.size());
	}
}
