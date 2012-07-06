package com.google.code.gaeom.impl;

import java.util.Map;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.ReadPolicy;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.common.collect.MapMaker;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectStoreImpl implements ObjectStore
{
	final private ObjectStoreCache storeCache = new ObjectStoreCache();
	final private Map<Class<?>, ObjectEncoder> mapperMap = new MapMaker().makeMap();
	final private Map<String, Class<?>> kindToType = new MapMaker().makeMap();
	final private Map<Class<?>, String> typeToKind = new MapMaker().makeMap();

	public ObjectStoreImpl()
	{
	}

	public ObjectStoreCache getStoreCache()
	{
		return storeCache;
	}

	@Override
	public ObjectStoreSession beginSession()
	{
		return beginSession(DatastoreServiceConfig.Builder.withReadPolicy(new ReadPolicy(ReadPolicy.Consistency.EVENTUAL)));
	}

	@Override
	public ObjectStoreSession beginSession(DatastoreServiceConfig config)
	{
		return beginSession(DatastoreServiceFactory.getAsyncDatastoreService(config));
	}

	@Override
	public ObjectStoreSession beginSession(AsyncDatastoreService service)
	{
		return new ObjectStoreSessionImpl(this, service);
	}

	@Override
	public void register(String kind, Class<?> type)
	{
		typeToKind.put(type, kind);
		kindToType.put(kind, type);
	}

	public String typeToKind(Class<?> clazz)
	{
		String kind = typeToKind.get(clazz);
		if (kind == null)
		{
			kind = clazz.getName().replaceAll("_", "__");
			kind = kind.replaceAll("\\.", "_");
			register(kind, clazz);
		}
		return kind;
	}

	public <T> Class<T> kindToType(String kind)
	{
		Class<?> type = kindToType.get(kind);
		if (type == null)
		{
			String name = kind.replaceAll("_", ".");
			name = name.replaceAll("\\.\\.", "_");
			try
			{
				type = Class.forName(name);
			}
			catch (ClassNotFoundException e)
			{
				throw new IllegalStateException(e);
			}
			register(kind, type);
		}
		return ObjectUtils.cast(type);
	}

	public ObjectEncoder getEncoder(Class<?> clazz)
	{
		ObjectEncoder mapper = mapperMap.get(clazz);
		if (mapper == null)
		{
			mapper = new ObjectEncoder(this, clazz);
			mapperMap.put(clazz, mapper);
		}
		return mapper;
	}

	@Override
	public void clearCache()
	{
		storeCache.clear();		
	}
}
