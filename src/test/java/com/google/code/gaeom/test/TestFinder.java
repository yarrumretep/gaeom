package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.code.gaeom.Find;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestFinder extends AbstractLocalTest
{
	public static class Person
	{
		public String firstName;
		public String lastName;
		public int number;

		public Person()
		{
		}

		public Person(String firstName, String lastName, int number)
		{
			super();
			this.firstName = firstName;
			this.lastName = lastName;
			this.number = number;
		}

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
			result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Person other = (Person) obj;
			if (firstName == null)
			{
				if (other.firstName != null)
					return false;
			}
			else if (!firstName.equals(other.firstName))
				return false;
			if (lastName == null)
			{
				if (other.lastName != null)
					return false;
			}
			else if (!lastName.equals(other.lastName))
				return false;
			return true;
		}
	}

	private static final Person cFredFlintstone = new Person("Fred", "Flintsonte", 1);
	private static final Person cSallyStruthers = new Person("Sally", "Struthers", 2);
	private static final Person cWilmaFlintstone = new Person("Wilma", "Flintsonte", 3);
	private static final Person cBarneyRubble = new Person("Barney", "Rubble", 4);
	private static final Person cBettyRubble = new Person("Betty", "Rubble", 5);
	private static final Person cBamBamRubble = new Person("BamBam", "Rubble", 6);

	private static List<Person> allPeople = Lists.newArrayList(cFredFlintstone, cSallyStruthers, cWilmaFlintstone, cBarneyRubble, cBettyRubble, cBamBamRubble);

	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Before
	public void load()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		oss.store(allPeople).now();
	}

	@Test
	public void testFindAll()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).now());
		assertEquals(allPeople, people);
	}

	@Test
	public void testLimit()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).sort("firstName").limit(3).now());
		assertEquals(3, people.size());
		assertEquals(Lists.newArrayList(cBamBamRubble, cBarneyRubble, cBettyRubble), people);
	}

	@Test
	public void testLimitDescending()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).sort("firstName", Find.Sort.Descending).limit(3).now());
		assertEquals(3, people.size());
		assertEquals(Lists.newArrayList(cWilmaFlintstone, cSallyStruthers, cFredFlintstone), people);
	}

	@Test
	public void testOffset()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).sort("firstName").start(3).now());
		assertEquals(3, people.size());
		assertEquals(Lists.newArrayList(cFredFlintstone, cSallyStruthers, cWilmaFlintstone), people);
	}

	@Test
	public void testBeginsWithFilter()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filterBeginsWith("firstName", "B").sort("firstName").now());
		assertEquals(3, people.size());
		assertEquals(Lists.newArrayList(cBamBamRubble, cBarneyRubble, cBettyRubble), people);
	}

	@Test
	public void testEqualsFilter()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filter("firstName", "BamBam").now());
		assertEquals(1, people.size());
		assertEquals(Lists.newArrayList(cBamBamRubble), people);
	}

	@Test
	public void testEqualsFilter2()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filter("number", 3).now());
		assertEquals(1, people.size());
		assertEquals(Lists.newArrayList(cWilmaFlintstone), people);
	}

	@Test
	public void testEqualsFilter3()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filter("number", "3").now());
		assertEquals(1, people.size());
		assertEquals(Lists.newArrayList(cWilmaFlintstone), people);
	}

	@Test
	public void testComparatorFilter()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filter("firstName", Find.Op.LessThan, "Barney").now());
		assertEquals(1, people.size());
		assertEquals(Lists.newArrayList(cBamBamRubble), people);
	}

	@Test
	public void testInFilter()
	{
		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();
		List<Person> people = Lists.newArrayList(oss.find(Person.class).filterIn("firstName", Lists.newArrayList("Barney", "Wilma")).now());
		assertEquals(Lists.newArrayList(cBarneyRubble, cWilmaFlintstone), people);
	}
}
