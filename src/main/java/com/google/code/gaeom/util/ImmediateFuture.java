package com.google.code.gaeom.util;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public abstract class ImmediateFuture<T> implements Future<T>
{
	public boolean cancel(boolean arg0)
	{
		return false;
	}

	public abstract T get();

	public T get(long arg0, TimeUnit arg1)
	{
		return get();
	}

	public boolean isCancelled()
	{
		return false;
	}

	public boolean isDone()
	{
		return true;
	}
}
