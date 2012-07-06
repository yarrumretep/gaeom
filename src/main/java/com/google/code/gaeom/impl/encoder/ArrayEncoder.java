package com.google.code.gaeom.impl.encoder;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.ConvertUtils;

import com.google.code.gaeom.impl.FieldEncoder;
import com.google.code.gaeom.impl.InstanceSource;
import com.google.code.gaeom.impl.InstanceStore;
import com.google.code.gaeom.impl.PropertySource;
import com.google.code.gaeom.impl.PropertyStore;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.common.collect.Lists;

public class ArrayEncoder extends CollectionEncoder
{
	final Class<?> componentType;

	public ArrayEncoder(String prefix, Class<?> componentType, FieldEncoder encoder)
	{
		super(prefix, ArrayList.class, encoder);
		this.componentType = componentType;
	}

	@Override
	public Object decode(InstanceSource instanceSource, PropertySource source)
	{
		List<Object> list = ObjectUtils.cast(super.decode(instanceSource, source));
		Object array = Array.newInstance(componentType, list.size());
		for (int ct = 0; ct < list.size(); ct++)
			Array.set(array, ct, ConvertUtils.convert(list.get(ct), componentType));
		return array;
	}

	@Override
	public void encode(InstanceStore instanceStore, Object value, PropertyStore store)
	{
		List<Object> list = Lists.newArrayList();
		int end = Array.getLength(value);
		for (int ct = 0; ct < end; ct++)
			list.add(Array.get(value, ct));
		super.encode(instanceStore, list, store);
	}
}
