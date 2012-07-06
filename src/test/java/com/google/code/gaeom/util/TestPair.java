package com.google.code.gaeom.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestPair
{
	@Test
	public void testPair()
	{
		Pair<String, String> pair = Pair.create("Foo", "Bar");
		assertEquals("Foo", Pair.<String, String> firstFunction().apply(pair));
		assertEquals("Bar", Pair.<String, String> secondFunction().apply(pair));
	}
}
