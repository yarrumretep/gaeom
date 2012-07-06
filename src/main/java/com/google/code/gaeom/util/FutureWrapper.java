package com.google.code.gaeom.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public abstract class FutureWrapper<V> implements Future<V>
{
	Future<?> wrapped;

	public FutureWrapper(Future<?> wrapped)
	{
		this.wrapped = wrapped;
	}

	public boolean cancel(boolean mayInterruptIfRunning)
	{
		return wrapped.cancel(mayInterruptIfRunning);
	}

	public V get() throws InterruptedException, ExecutionException
	{
		try
		{
			return get(Long.MAX_VALUE, TimeUnit.DAYS);
		}
		catch (TimeoutException e)
		{
			throw new RuntimeException("Should not happen!", e);
		}
	}

	public boolean isCancelled()
	{
		return wrapped.isCancelled();
	}

	public boolean isDone()
	{
		return wrapped.isDone();
	}
}
