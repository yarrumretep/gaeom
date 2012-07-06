package com.google.code.gaeom.util;

import com.google.common.base.Function;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class Pair<X, Y>
{
	final X first;
	final Y second;

	public static <X, Y> Function<Pair<X, Y>, X> firstFunction()
	{
		return new Function<Pair<X, Y>, X>()
		{
			@Override
			public X apply(Pair<X, Y> input)
			{
				return input.getFirst();
			}
		};
	}

	public static <X, Y> Function<Pair<X, Y>, Y> secondFunction()
	{
		return new Function<Pair<X, Y>, Y>()
		{
			@Override
			public Y apply(Pair<X, Y> input)
			{
				return input.getSecond();
			}
		};
	}

	public Pair(X first, Y second)
	{
		this.first = first;
		this.second = second;
	}

	public X getFirst()
	{
		return first;
	}

	public Y getSecond()
	{
		return second;
	}

	public static <X, Y> Pair<X, Y> create(X first, Y second)
	{
		return new Pair<X, Y>(first, second);
	}
}
