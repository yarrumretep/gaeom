package com.google.code.gaeom.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

public class TestFutureUtils
{
	@Test(expected = IllegalStateException.class)
	public void testFutureUtils()
	{
		FutureUtils.safeGet(new Future<Void>()
		{

			@Override
			public boolean cancel(boolean mayInterruptIfRunning)
			{
				return false;
			}

			@Override
			public boolean isCancelled()
			{
				return false;
			}

			@Override
			public boolean isDone()
			{
				return false;
			}

			@Override
			public Void get() throws InterruptedException, ExecutionException
			{
				throw new InterruptedException("TEST");
			}

			@Override
			public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
			{
				throw new RuntimeException("Should not happen");
			}
		});
	}

	@Test(expected = IllegalStateException.class)
	public void testFutureUtils2()
	{
		FutureUtils.safeGet(new Future<Void>()
		{

			@Override
			public boolean cancel(boolean mayInterruptIfRunning)
			{
				return false;
			}

			@Override
			public boolean isCancelled()
			{
				return false;
			}

			@Override
			public boolean isDone()
			{
				return false;
			}

			@Override
			public Void get() throws InterruptedException, ExecutionException
			{
				throw new ExecutionException(new UnsupportedClassVersionError());
			}

			@Override
			public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
			{
				throw new RuntimeException("Should not happen");
			}
		});
	}
}
