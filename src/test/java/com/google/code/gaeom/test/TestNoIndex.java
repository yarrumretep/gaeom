package com.google.code.gaeom.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.NoIndex;

public class TestNoIndex extends AbstractLocalTest
{
	public static class Dork
	{
		@NoIndex
		int v1;

		int v2;
	}

	@Test
	public void testNoIndex()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();

		Dork d = new Dork();
		d.v1 = 12;
		d.v2 = 12;

		oss.store(d).now();

		Dork d1 = oss.find(Dork.class).filter("v1", 12).single().now();
		assertNull(d1);

		Dork d2 = oss.find(Dork.class).filter("v2", 12).single().now();
		assertNotNull(d2);
	}
}
