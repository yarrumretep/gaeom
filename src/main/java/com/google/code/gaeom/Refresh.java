package com.google.code.gaeom;

/**
 * The interface for the Refresh command instances.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Refresh<T> extends LoadOrRefresh<Refresh<T>>, Terminator<T>
{
}
