package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Query;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestNameRegistration extends AbstractLocalTest
{
	public static class Foo
	{
		String name;

		public Foo(String name)
		{
			this.name = name;
		}
	}

	protected boolean simulateEventualConsistency()
	{
		return false;
	}
	
	@Test
	public void testNameRegistration()
	{
		ObjectStore os = ObjectStore.Factory.create();
		os.register("Foo", Foo.class);

		ObjectStoreSession oss = os.beginSession();
		oss.store(new Foo("Fred Flintstone")).now();
		oss.store(new Foo("Barney Rubble")).now();

		List<Foo> foos = Lists.newArrayList(oss.find(Foo.class).now());
		assertEquals(2, foos.size());

		DatastoreService service = DatastoreServiceFactory.getDatastoreService();
		List<Entity> entities = Lists.newArrayList(service.prepare(new Query("Foo")).asIterable());
		assertEquals(2, entities.size());
		for(Entity e : entities)
		{
			String name = e.getProperty("name").toString();
			assertTrue (name.equals("Fred Flintstone") || name.equals("Barney Rubble"));
		}
	}
}
