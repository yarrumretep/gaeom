package com.google.code.gaeom;

import java.util.concurrent.Future;

/**
 * Interface to load objects given a pre existing Key instance
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface LoadByKey<C extends LoadByKey<C>> extends Load<C>
{
	/**
	 * Interface to load a single object by key.
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Single extends LoadByKey<Single>
	{
		/**
		 * Executes the load command synchronously
		 * 
		 * @return the loaded instance
		 */
		<T> T now();

		/**
		 * Executes the load command asynchronously
		 * 
		 * @return a Future for the loaded instance
		 */
		<T> Future<T> later();
	}

	/**
	 * Interface to load multiple objects by key.s
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Multi extends LoadByKey<Multi>
	{
		/**
		 * Executes the load command synchronously
		 * 
		 * @return an Iterable of the loaded objects
		 */
		<T> Iterable<T> now();

		/**
		 * Executes the load command asynchronously
		 * 
		 * @return a Future for an Iterable of loaded objects;
		 */
		<T> Future<Iterable<T>> later();
	}
}
