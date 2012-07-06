package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestEmbedding extends AbstractLocalTest
{
	public static class Embedder
	{
		String name;
		@Embedded
		Embeddee embeddee;
	}

	public static class Embeddee
	{
		Long size;
		String description;
		@Embedded
		Embeddee2 ork;
	}
	
	public static class Embeddee2
	{
		String foo;
		Long goo;
	}

	@Test
	public void testEmbedding()
	{
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Embedder e = new Embedder();
		e.name = "Fred Flintstone";
		Embeddee em = new Embeddee();
		e.embeddee = em;
		e.embeddee.description = "Large size";
		e.embeddee.size = 55L;
		e.embeddee.ork = new Embeddee2();
		e.embeddee.ork.foo = "Ork";
		e.embeddee.ork.goo = 44L;

		oss.store(e).now();

		e.name = null;
		e.embeddee = null;
		
		oss.refresh(e).now();

		assertTrue(em != e.embeddee);
		assertEquals("Fred Flintstone", e.name);
		assertEquals("Large size", e.embeddee.description);
		assertEquals(55L, e.embeddee.size.longValue());
		assertEquals("Ork", e.embeddee.ork.foo);
		assertEquals(44L, e.embeddee.ork.goo.longValue());
	}
}
