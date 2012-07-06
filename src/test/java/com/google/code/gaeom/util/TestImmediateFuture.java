package com.google.code.gaeom.util;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TestImmediateFuture
{
	@Test
	public void testImmediateFuture()
	{
		ImmediateFuture<Void> immed = new ImmediateFuture<Void>()
		{
			@Override
			public Void get()
			{
				return null;
			}
		};

		assertFalse(immed.cancel(true));
		assertFalse(immed.isCancelled());
		assertTrue(immed.isDone());
		assertTrue(immed.get() == null);
		assertTrue(immed.get(10, TimeUnit.DAYS) == null);
	}
}
