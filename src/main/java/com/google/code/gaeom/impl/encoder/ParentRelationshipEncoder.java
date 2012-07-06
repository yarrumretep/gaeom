package com.google.code.gaeom.impl.encoder;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ParentRelationshipEncoder implements FieldEncoder
{
	final String fieldName;

	public ParentRelationshipEncoder(String fieldName)
	{
		this.fieldName = fieldName;
	}

	@Override
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		return instanceSource.fromKey(fieldName, instanceSource.getCurrentKey().getParent());
	}

	@Override
	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		// no need to encode - information is in parent key
	}
}
