package com.google.code.gaeom.util;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class FutureUtils
{
	public static <T> T safeGet(Future<T> future)
	{
		try
		{
			return future.get();
		}
		catch (InterruptedException e)
		{
			throw new IllegalStateException(e);
		}
		catch (ExecutionException e)
		{
			throw new IllegalStateException(e);
		}
	}
}
