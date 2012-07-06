package com.google.code.gaeom.test;

import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.RandomStringUtils;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.annotation.Text;

public class TestTextEncoder extends AbstractLocalTest
{
	public static class Foo
	{
		@Text
		String longString;
	}

	@Test
	public void testTextAnnotation() throws Exception
	{
		Foo f = new Foo();
		f.longString = RandomStringUtils.randomAlphanumeric(800);

		Key key = ObjectStore.Singleton.get().beginSession().store(f).now();

		Entity e = DatastoreServiceFactory.getDatastoreService().get(key);
		assertTrue(e.getProperty("longString") instanceof com.google.appengine.api.datastore.Text);
	}
}
