package com.google.code.gaeom.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestSimpleStoreLoad extends AbstractLocalTest
{
	public static class Person
	{
		String firstName;
		String lastName;
		Long shoeSize;
	}

	@Test
	public void testSimpleStoreLoad()
	{
		ObjectStore osf = ObjectStore.Factory.create();
		ObjectStoreSession os = osf.beginSession();

		Person p1 = new Person();

		p1.firstName = "Fred";
		p1.lastName = "Flintstone";
		p1.shoeSize = 12L;

		Key key = os.store(p1).id(33L).now();
		assertEquals(33L, key.getId());
		
		ObjectStoreSession os2 = osf.beginSession();
		Person p2 = (Person) os2.load(Person.class).id(33L).now();

		assertTrue(p2 != p1);
		assertEquals(p1.firstName, p2.firstName);
		assertEquals(p1.lastName, p2.lastName);
		assertEquals(p1.shoeSize, p2.shoeSize);
		
		p1.firstName = null;
		p1.lastName = null;
		p1.shoeSize = null;
		os.refresh(p1).now();
		assertTrue(p2 != p1);
		assertEquals(p1.firstName, p2.firstName);
		assertEquals(p1.lastName, p2.lastName);
		assertEquals(p1.shoeSize, p2.shoeSize);
	}
}
