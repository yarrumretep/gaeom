package com.google.code.gaeom.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestFieldFunction
{
	public static class Foo
	{
		String a;
		String b;
		String c;
	}

	@Test
	public void testFieldFunction()
	{
		Foo foo = new Foo();
		foo.a = "a";
		foo.b = "b";
		foo.c = "c";

		assertEquals("a", FieldFunction.<String> create("a").apply(foo));
		assertEquals("b", FieldFunction.<String> create("b").apply(foo));
		assertEquals("c", FieldFunction.<String> create("c").apply(foo));
	}
}
