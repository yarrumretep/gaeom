package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;
import com.google.code.gaeom.annotation.EmbeddedIn;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestEmbeddedIn extends AbstractLocalTest
{
	public static class Embedder
	{
		@Embedded
		Embeddee embeddee;
		String name;
	}
	
	public static class Embeddee
	{
		@EmbeddedIn
		Embedder embedder;
		String foo;
		@Embedded
		Embeddeeee embeddeeee;
	}
	
	public static class Embeddeeee
	{
		@EmbeddedIn
		Embeddee embeddee;
		@EmbeddedIn
		Embedder embedder;
	}
	
	@Test
	public void testEmbeddedIn()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		
		Embedder embedder = new Embedder();
		embedder.name = "Fred";
		embedder.embeddee = new Embeddee();
		embedder.embeddee.embedder = embedder;
		embedder.embeddee.foo = "Foo";
		embedder.embeddee.embeddeeee = new Embeddeeee();
		embedder.embeddee.embeddeeee.embeddee = embedder.embeddee;
		embedder.embeddee.embeddeeee.embedder = embedder;
		
		oss.store(embedder).now();
		
		embedder.name = null;
		embedder.embeddee = null;
		
		oss.refresh(embedder).now();
		
		assertEquals(embedder, embedder.embeddee.embedder);
		assertEquals(embedder, embedder.embeddee.embeddeeee.embedder);
		assertEquals(embedder.embeddee, embedder.embeddee.embeddeeee.embeddee);
	}
}
