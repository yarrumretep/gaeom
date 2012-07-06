package com.google.code.gaeom.impl;

import com.google.appengine.api.datastore.Key;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface InstanceSource
{
	public Key getCurrentKey();

	public Object fromKey(String fieldName, Key key);
}
