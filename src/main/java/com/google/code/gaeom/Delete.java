package com.google.code.gaeom;

/**
 * The Delete command interface. This command results from calls to {@link ObjectStoreSession#delete(Iterable)} and its
 * ilk. The command must be terminated by a call to {@link #now()} or {@link #later()} to specify synchronous or
 * asynchronous operation.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Delete extends Terminator<Void>
{
}
