package com.google.code.gaeom.test;

import java.util.Set;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang.math.RandomUtils;
import org.junit.Test;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.Find;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Parent;
import com.google.common.collect.Sets;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class TestEnumEncoder extends AbstractLocalTest
{
	public static enum Foo
	{
		A, B, C
	}

	public static class EnumObject
	{
		Foo value = Foo.values()[RandomUtils.nextInt(Foo.values().length)];

		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((value == null) ? 0 : value.hashCode());
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
			EnumObject other = (EnumObject) obj;
			if (value != other.value)
				return false;
			return true;
		}
	}

	protected boolean simulateEventualConsistency()
	{
		return false;
	}

	@Test
	public void testEnumEncoder()
	{
		Set<EnumObject> objs = Sets.newHashSet();
		for (int ct = 0; ct < 10; ct++)
			objs.add(new EnumObject());

		ObjectStore os = ObjectStore.Factory.create();
		ObjectStoreSession oss = os.beginSession();
		oss.store(objs).now();

		// will get new objects b/c we are using a new session
		Set<EnumObject> result = Sets.newHashSet(os.beginSession().find(EnumObject.class).now());

		assertEquals(objs, result);
	}

	public static class FindEnums
	{
		Find.Op op;
		Find.Sort sort;
		Parent.FilterPolicy policy;
	}

	@Test
	public void testSavingFinderEnums()
	{
		// this test is redundant but gets us some coverage
		FindEnums fe = new FindEnums();
		fe.op = Find.Op.EqualTo;
		fe.sort = Find.Sort.Descending;
		fe.policy = Parent.FilterPolicy.AncestorQuery;

		Key key = ObjectStore.Factory.create().beginSession().store(fe).now();

		FindEnums fe2 = ObjectStore.Factory.create().beginSession().load(key).now();

		assertEquals(fe.op, fe2.op);
		assertEquals(fe.sort, fe2.sort);
		assertEquals(fe.policy, fe2.policy);
	}
}
