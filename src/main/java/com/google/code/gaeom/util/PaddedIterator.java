package com.google.code.gaeom.util;

import java.util.Iterator;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class PaddedIterator<E> implements Iterator<E>
{
	final Iterator<E> wrapped;
	final E padding;
	
	public static <E> PaddedIterator<E> pad(Iterator<E> iterator)
	{
		return pad(iterator, null);
	}
	
	public static <E> PaddedIterator<E> pad(Iterator<E> iterator, E value)
	{
		return new PaddedIterator<E>(iterator, value);
	}
	
	private PaddedIterator(Iterator<E> wrapped, E value)
	{
		this.wrapped = wrapped;
		this.padding = value;
	}

	public boolean hasNext()
	{
		return true;
	}

	public E next()
	{
		if(wrapped.hasNext())
			return wrapped.next();
		else
			return padding;
	}

	public void remove()
	{
		throw new UnsupportedOperationException();
	}
}
