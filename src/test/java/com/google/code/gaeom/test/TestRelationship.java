package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.KeysNotFoundException;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestRelationship extends AbstractLocalTest
{
	public static class A
	{
		String name;
		B b = new B();
	}

	public static class B
	{
		String name;
	}

	@Test
	public void testSimpleRelationship()
	{
		A a = new A();

		a.name = "Fred";
		a.b.name = "Flintstone";

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Key key = oss.store(a).now();

		ObjectStoreSession oss2 = os.beginSession();

		A a2 = (A) oss2.load(key).now();

		assertEquals(a.name, a2.name);
		assertNotNull(a2.b);
		assertEquals(a.b.name, a2.b.name);
	}

	@Test(expected = KeysNotFoundException.class)
	public void testFailedRelationship()
	{
		A a = new A();

		a.name = "Fred";
		a.b.name = "Flintstone";

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Key key = oss.store(a).now();
		oss.delete(a.b).now();

		ObjectStoreSession oss2 = os.beginSession();

		oss2.load(key).retries(1).now();
		assertTrue(false);
	}

	public static class Family
	{
		String name;
		List<Person> heads = Lists.newArrayList();
		List<Person> children = Lists.newArrayList();

		Family()
		{
		}

		public Family(String name)
		{
			this.name = name;
		}

		public void addHead(Person head)
		{
			heads.add(head);
			head.family = this;
		}

		public void addChild(Person child)
		{
			children.add(child);
			child.family = this;
		}
	}

	public static class Person
	{
		Family family;
		String name;

		Person()
		{
		}

		Person(String name)
		{
			this.name = name;
		}
	}

	@Test
	public void testMoreComplexRelationships()
	{
		Family family = new Family("Murray");
		family.addHead(new Person("Pete"));
		family.addHead(new Person("Kim"));
		family.addChild(new Person("Sam"));
		family.addChild(new Person("Maggie"));
		family.addChild(new Person("Katy"));

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();

		Key key = oss.store(family).now();

		ObjectStoreSession oss2 = os.beginSession();

		Family family2 = (Family) oss2.load(key).now();

		assertEquals(family.name, family2.name);
		assertEquals(family.heads.size(), family2.heads.size());
		assertEquals(family.children.size(), family2.children.size());
		assertEquals(family.heads.get(0).name, family2.heads.get(0).name);
		assertEquals(family.heads.get(1).name, family2.heads.get(1).name);
		assertEquals(family.children.get(0).name, family2.children.get(0).name);
		assertEquals(family.children.get(1).name, family2.children.get(1).name);
		assertEquals(family.children.get(2).name, family2.children.get(2).name);
	}
}
