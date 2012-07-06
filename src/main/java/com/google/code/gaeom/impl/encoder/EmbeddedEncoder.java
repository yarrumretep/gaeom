package com.google.code.gaeom.impl.encoder;

import java.util.Map;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.ObjectEncoder;
import com.google.code.gaeom.impl.ObjectStoreImpl;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class EmbeddedEncoder implements FieldEncoder
{
	private final String propertyPrefix;
	private final String typeProperty;
	private final String nullProperty;
	private final Class<?> clazz;
	private final ObjectStoreImpl store;
	private final Map<Class<?>, ObjectEncoder> encoders = Maps.newConcurrentMap();

	public EmbeddedEncoder(String propertyName, Class<?> clazz, ObjectStoreImpl store)
	{
		this.propertyPrefix = propertyName;
		this.typeProperty = propertyName + ".__type__";
		this.nullProperty = propertyName + ".__null__";
		this.clazz = clazz;
		this.store = store;
	}

	private ObjectEncoder getEncoder(Class<?> clazz)
	{
		ObjectEncoder encoder = encoders.get(clazz);
		if (encoder == null)
		{
			encoder = new ObjectEncoder(store, clazz, propertyPrefix);
			encoders.put(clazz, encoder);
		}
		return encoder;
	}

	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		Boolean isNull = source.getProperty(nullProperty);
		if (isNull != null && isNull)
		{
			return null;
		}
		else
		{
			Class<?> objectClass;
			String typeName = source.getProperty(typeProperty);
			if (typeName != null)
				objectClass = store.kindToType(typeName);
			else
				objectClass = clazz;
			Object o = ObjectUtils.newInstance(objectClass);
			getEncoder(objectClass).decode(instanceSource, o, source);
			return o;
		}
	}

	public void encode(InstanceStore instanceStore, Object value, PropertyStore propertyStore)
	{
		if (value == null)
		{
			propertyStore.setProperty(nullProperty, Boolean.TRUE, true);
		}
		else
		{
			Class<?> objectClass = value.getClass();
			if (clazz != objectClass)
				propertyStore.setProperty(typeProperty, store.typeToKind(objectClass), true);
			getEncoder(value.getClass()).encode(instanceStore, value, propertyStore);
		}
	}

	public ObjectEncoder getDefaultEncoder()
	{
		return getEncoder(clazz);
	}
}
