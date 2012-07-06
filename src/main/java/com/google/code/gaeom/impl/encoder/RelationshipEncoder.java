package com.google.code.gaeom.impl.encoder;

import com.google.appengine.api.datastore.Key;
import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.ObjectEncoder;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class RelationshipEncoder implements FieldEncoder
{
	private final String fieldName;
	private final String property;
	private final boolean child;
	private final boolean index;
	
	public RelationshipEncoder(String fieldName, String property, boolean child, boolean index)
	{
		this.fieldName = fieldName;
		this.child = child;
		this.property = property;
		this.index = index;
	}

	@Override
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		Key key = source.getProperty(property);
		if (key == null)
			return null;
		else
			return instanceSource.fromKey(fieldName, key);
	}

	@Override
	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		if (value != null)
			store.setProperty(property, instanceStore.toKey(value, child ? ObjectEncoder.getObjectStack().peek() : null), index);
		else
			store.setProperty(property, null, index);
	}
}
