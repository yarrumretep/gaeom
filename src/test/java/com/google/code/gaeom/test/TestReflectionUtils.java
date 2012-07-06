package com.google.code.gaeom.test;

import java.util.List;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.code.gaeom.test.util.Generator;
import com.google.code.gaeom.util.ReflectionUtils;
import com.google.common.collect.Lists;

public class TestReflectionUtils
{
	public static class Foo
	{
		String name;
		Bar bar;
		List<Bat> bats = Lists.newArrayList();
	}

	public static class Bar
	{
		String name;
	}

	public static class Bat
	{
		String gack;
	}

	@Test
	public void testFieldsEqual()
	{
		Foo foo = Generator.generate(Foo.class).generate();

		assertTrue(ReflectionUtils.fieldsEqual(foo, foo));
	}
}
