package com.google.code.gaeom.impl;

import com.google.code.gaeom.Terminator;
import com.google.code.gaeom.util.FutureUtils;

public abstract class AbstractTerminator<T> implements Terminator<T>
{
	@Override
	public T now()
	{
		return FutureUtils.safeGet(later());
	}
}
