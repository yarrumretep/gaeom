package com.google.code.gaeom.performance;

import java.util.List;

import static org.junit.Assert.assertTrue;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.annotation.Embedded;
import com.google.code.gaeom.test.util.Generator;
import com.google.code.gaeom.util.ReflectionUtils;
import com.google.common.collect.Lists;

public class PerfLoad10000 extends AbstractPerformanceTest
{
	public static class Foo
	{
		String name;
		Long shoeSize;
		@Embedded
		Bar bar;
		@Embedded
		List<Bar>bars;
	}

	public static class Bar
	{
		int bork;
		int ork;
		double dork;
	}

	List<Foo> originals;
	List<Key> keys;

	@Override
	protected void onceBefore() throws Throwable
	{
		originals = Generator.generate(Foo.class).count(10000).generate();
	}

	@Override
	public void before()
	{
		keys = createSession().store(originals).now();
	}

	List<Foo> foos;

	@Override
	protected void test() throws Throwable
	{
		foos = Lists.newArrayList(createSession().load(keys).<Foo>now());
	}

	@Override
	protected void after()
	{
		assertTrue(ReflectionUtils.fieldsEqual(originals, foos));
	}
}
