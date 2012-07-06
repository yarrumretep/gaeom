package com.google.code.gaeom.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestActivationFilter
{
	@Test
	public void testDefaultFilter()
	{
		ActivationFilter filter = ActivationFilter.compile();
		assertTrue(filter == null);
	}

	@Test
	public void testDefaultFilter2()
	{
		ActivationFilter filter = ActivationFilter.compile("**");
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("foo", "bar", "base"));
	}

	@Test
	public void testNoActivateFilter()
	{
		ActivationFilter filter = ActivationFilter.compile("");
		assertFalse(filter.accept());
		assertFalse(filter.accept("members", "sponsor"));
		assertFalse(filter.accept("foo", "bar", "base"));
	}

	@Test
	public void testPatternFilter()
	{
		ActivationFilter filter = ActivationFilter.compile("members.sponsor");
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertFalse(filter.accept("foo", "bar", "base"));
	}

	@Test
	public void testPatternFilter2()
	{
		ActivationFilter filter = ActivationFilter.compile("*.sponsor");
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("foogy", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor"));
		assertFalse(filter.accept("foo", "bar", "base"));
	}

	@Test
	public void testPatternFilter3()
	{
		ActivationFilter filter = ActivationFilter.compile("*.*");
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor"));
		assertFalse(filter.accept("foo", "bar", "base"));
	}

	@Test
	public void testLevelFilter()
	{
		ActivationFilter filter = ActivationFilter.compile(ActivationFilter.getLevelActivationFilter(2));
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor"));
		assertFalse(filter.accept("foo", "bar", "base"));
	}
	
	@Test
	public void testMultiFilter()
	{
		ActivationFilter filter = ActivationFilter.compile("**.sponsor");
		assertTrue(filter.accept());
		assertTrue(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor"));
		assertTrue(filter.accept("foo", "sponsor", "base", "sponsor"));
	}
	
	@Test
	public void testMultiFilter2()
	{
		ActivationFilter filter = ActivationFilter.compile("blah.**");
		assertTrue(filter.accept());
		assertFalse(filter.accept("members", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor", "base", "sponsor"));
	}

	@Test
	public void testMultiFilter3()
	{
		ActivationFilter filter = ActivationFilter.compile("blah.**.base.*");
		assertTrue(filter.accept());
		assertFalse(filter.accept("members", "sponsor"));
		assertFalse(filter.accept("blah", "sponsor"));
		assertTrue(filter.accept("blah", "sponsor", "base", "sponsor"));
	}

	@Test
	public void testMultiFilter4()
	{
		ActivationFilter filter = ActivationFilter.compile("**.children");
		assertTrue(filter.accept("children"));
		assertTrue(filter.accept("children", "children"));
		assertTrue(filter.accept("children", "children", "children", "children"));
		assertFalse(filter.accept("children", "children", "children", "Bob"));
	}

	@Test
	public void testAggregateFilter1()
	{
		ActivationFilter filter = ActivationFilter.compile("**.children", "**.parent", "!**.parent.children");
		assertTrue(filter.accept("children"));
		assertTrue(filter.accept("parent"));
		assertTrue(filter.accept("children", "children"));
		assertTrue(filter.accept("children", "parent"));
		assertTrue(filter.accept("children", "children", "parent"));
		assertFalse(filter.accept("children", "children", "parent", "children"));
	}
}
