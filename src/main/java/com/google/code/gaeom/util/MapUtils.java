package com.google.code.gaeom.util;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class MapUtils
{
	public static <K, V> Map<K, V> createMap(K key1, V value1)
	{
		return createMap(key1, value1, null, null, null, null);
	}

	public static <K, V> Map<K, V> createMap(K key1, V value1, K key2, V value2)
	{
		return createMap(key1, value1, key2, value2, null, null);
	}

	public static <K, V> Map<K, V> createMap(K key1, V value1, K key2, V value2, K key3, V value3)
	{
		Map<K, V> map = Maps.newHashMap();
		if (key1 != null)
			map.put(key1, value1);
		if (key2 != null)
			map.put(key2, value2);
		if (key3 != null)
			map.put(key3, value3);
		return map;
	}
}
