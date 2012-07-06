package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Id;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestIdField extends AbstractLocalTest
{
	public static class LongId
	{
		@Id
		Long id;
		String name;
	}
	
	@Test
	public void testGeneratedIdField()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		LongId o = new LongId();
		o.name = "Fred";
		oss.store(o).now();
		
		assertNotNull(o.id);
		Long id = o.id;
		
		o.id = null;
		o.name = null;
		
		oss.refresh(o).now();
		
		assertEquals(id, o.id);
		assertEquals("Fred", o.name);
	}
}
