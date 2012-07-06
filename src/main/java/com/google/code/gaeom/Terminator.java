package com.google.code.gaeom;

import java.util.concurrent.Future;

/**
 * Interface that adds asynchronous operation of commands for certain commands.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Terminator<T> 
{
	/**
	 * Execute this command synchronously now and return the results.
	 * 
	 * @return the results of executing the command.
	 */
	public T now();

	/**
	 * Execute the command asynchronously now and return a Future from which to obtain the result later.
	 * 
	 * @return a Future for the result of the command.
	 */
	Future<T> later();
}
