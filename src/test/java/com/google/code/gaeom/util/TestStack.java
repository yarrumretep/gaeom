package com.google.code.gaeom.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestStack
{
	@Test
	public void testStack()
	{
		Stack.Mutable<String> stack = Stack.Factory.create();

		stack.push("Foo");
		stack.push("Bar");

		Stack<String> immutable = stack;
		
		assertEquals("Bar", immutable.peek());
		assertEquals("Foo", immutable.peek(1));
		assertEquals(2, immutable.depth());
		
		assertEquals("Bar", stack.peek());
		assertEquals("Foo", stack.peek(1));
		assertEquals(2, stack.depth());
		
		
		assertEquals("Bar", stack.pop());
		assertEquals(1, stack.depth());
		assertEquals("Foo", stack.pop());
		assertEquals(0, stack.depth());
		
	}
}
