package com.google.code.gaeom.impl;

import java.util.Iterator;

class LongIterable implements Iterable<Long>
{
	private final long[] ids;

	LongIterable(long... ids)
	{
		this.ids = ids;
	}

	@Override
	public Iterator<Long> iterator()
	{
		return new Iterator<Long>()
		{
			int ct = 0;

			@Override
			public boolean hasNext()
			{
				return ct < ids.length;
			}

			@Override
			public Long next()
			{
				return ids[ct++];
			}

			@Override
			public void remove()
			{
				throw new UnsupportedOperationException();
			}
		};
	}
}