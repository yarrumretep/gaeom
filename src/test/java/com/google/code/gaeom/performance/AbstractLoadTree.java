package com.google.code.gaeom.performance;

import org.junit.Assert;

import com.google.appengine.api.datastore.Key;

public abstract class AbstractLoadTree extends AbstractPerformanceTest
{
	Node original;
	Key key;

	
	@Override
	protected void onceBefore() throws Throwable
	{
		original = createTree();
	}

	@Override
	protected void before() throws Throwable
	{
		key = createSession().store(original).now();
	}

	Node copy;

	@Override
	protected void test() throws Throwable
	{
		copy = createSession().load(key).now();
	}

	@Override
	protected void after() throws Throwable
	{
		Assert.assertEquals(original, copy);
	}

	protected abstract Node createTree();
}
