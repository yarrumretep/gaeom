package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Child;
import com.google.code.gaeom.annotation.Parent;
import com.google.code.gaeom.annotation.Parent.FilterPolicy;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestParentRelationshipEncoding extends AbstractLocalTest
{
	public static class A
	{
		@Child
		B b;	
	}
	
	public static class B
	{
		@Parent(FilterPolicy.NoFilter)
		A a;
	}
		
	@Test
	public void testParenting()
	{
		A  a= new A();
		a.b = new B();
		a.b.a = a;
		
		ObjectStore os = ObjectStore.Factory.create();
		Key key = os.beginSession().store(a).now();
		
		A a2 = os.beginSession().load(key).now();
		
		assertEquals(a2, a2.b.a);
	}
	
	@Test
	public void testEntityIsEmpty() throws Exception
	{
		A  a= new A();
		a.b = new B();
		a.b.a = a;
		
		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(a).now();
		
		Entity e = DatastoreServiceFactory.getDatastoreService().get(oss.getKey(a.b));
		
		//should have no properties b/c parent is encoded in key
		assertEquals(0, e.getProperties().keySet().size());
	}
}
