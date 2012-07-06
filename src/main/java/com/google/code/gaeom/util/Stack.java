package com.google.code.gaeom.util;

import java.util.List;

import com.google.common.collect.Lists;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Stack<X>
{
	public static class Factory
	{
		public static <X> Mutable<X> create()
		{
			return new Mutable<X>()
			{
				final List<X> data = Lists.newArrayList();

				public X peek()
				{
					return peek(0);
				}

				public X peek(int depth)
				{
					return data.get(data.size() - 1 - depth);
				}

				public int depth()
				{
					return data.size();
				}

				public void push(X value)
				{
					data.add(value);
				}

				public X pop()
				{
					return data.remove(data.size() - 1);
				}
			};
		}
	}

	X peek();

	X peek(int depth);

	int depth();

	public interface Mutable<X> extends Stack<X>
	{
		void push(X value);

		X pop();
	}
}
