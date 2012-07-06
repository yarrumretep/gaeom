package com.google.code.gaeom.impl;

import java.util.Collection;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface PropertySource
{
	public Collection<String> getKeys();

	public <T> T getProperty(String key);
}
