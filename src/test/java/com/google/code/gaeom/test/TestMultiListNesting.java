package com.google.code.gaeom.test;

import java.util.List;

import org.junit.Test;

import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.annotation.Embedded;
import com.google.common.collect.Lists;

public class TestMultiListNesting extends AbstractLocalTest
{
	public static class A
	{
		@Embedded
		List<B> bs = Lists.newArrayList();
		String blah;
	}

	public static class B
	{
		@Embedded
		List<C> cs = Lists.newArrayList();
		String blah;
	}

	public static class C
	{
		String blah;
	}

	@Test(expected = IllegalArgumentException.class)
	public void testMultiNestingFailure()
	{
		A a = new A();

		a.bs.add(new B());
		a.bs.add(new B());

		a.bs.get(0).cs.add(new C());
		a.bs.get(0).cs.add(new C());
		a.bs.get(0).cs.add(new C());

		a.bs.get(1).cs.add(new C());
		a.bs.get(1).cs.add(new C());
		a.bs.get(1).cs.add(new C());
		a.bs.get(1).cs.add(new C());

		ObjectStoreSession oss = ObjectStore.Factory.create().beginSession();

		oss.store(a).now();

		a.bs = null;

		oss.refresh(a).now();

	}
}
