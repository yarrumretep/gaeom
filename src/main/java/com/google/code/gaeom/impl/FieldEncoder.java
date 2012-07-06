package com.google.code.gaeom.impl;


/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface FieldEncoder
{
	Object decode(InstanceSource instanceSource, PropertySource source);
	
	void encode(InstanceStore instanceStore, Object value, PropertyStore store);
}
