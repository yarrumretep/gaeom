package com.google.code.gaeom.impl;

import com.google.appengine.api.datastore.Key;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface InstanceStore
{
	public Key toKey(Object instance, Object parent);
}
