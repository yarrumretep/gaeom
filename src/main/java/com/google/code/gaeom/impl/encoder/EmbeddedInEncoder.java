package com.google.code.gaeom.impl.encoder;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.ObjectEncoder;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.code.gaeom.util.Stack;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class EmbeddedInEncoder implements FieldEncoder
{
	private final Class<?> embedderClass;
	
	public EmbeddedInEncoder(Class<?> embedderClass)
	{
		this.embedderClass = embedderClass;
	}
	
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		Stack<Object> stack = ObjectEncoder.getObjectStack();
		for(int ct = 1; ct < stack.depth(); ct++)
		{
			Object o = stack.peek(ct);
			if(embedderClass.isAssignableFrom(o.getClass()))
				return o;
		}
		return null;
	}

	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		// Nothing to do here
	}
}
