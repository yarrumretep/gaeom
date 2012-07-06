package com.google.code.gaeom.impl;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.google.appengine.api.datastore.FetchOptions.Builder.withOffset;

import org.apache.commons.beanutils.ConvertUtils;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.code.gaeom.Delete;
import com.google.code.gaeom.Find;
import com.google.code.gaeom.KeysNotFoundException;
import com.google.code.gaeom.LoadById;
import com.google.code.gaeom.LoadByKey;
import com.google.code.gaeom.ObjectStore;
import com.google.code.gaeom.ObjectStoreSession;
import com.google.code.gaeom.Refresh;
import com.google.code.gaeom.Store;
import com.google.code.gaeom.Terminator;
import com.google.code.gaeom.annotation.Parent;
import com.google.code.gaeom.impl.encoder.CollectionEncoder;
import com.google.code.gaeom.util.FutureUtils;
import com.google.code.gaeom.util.FutureWrapper;
import com.google.code.gaeom.util.ImmediateFuture;
import com.google.code.gaeom.util.ObjectUtils;
import com.google.code.gaeom.util.PaddedIterator;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * @author Peter Murray <gaeom@pmurray.com>
 */
public class ObjectStoreSessionImpl implements ObjectStoreSession
{
	final ObjectStoreImpl store;
	final ObjectStoreSessionCache cache;
	final AsyncDatastoreService service;

	public ObjectStoreSessionImpl(ObjectStoreImpl store, AsyncDatastoreService service)
	{
		this.store = store;
		this.cache = new ObjectStoreSessionCache(store.getStoreCache());
		this.service = service;
	}

	@Override
	public ObjectStore getObjectStore()
	{
		return store;
	}

	public Key getKey(Object object)
	{
		return cache.getKey(object);
	}

	@Override
	public boolean isActivated(Object object)
	{
		return cache.isActivated(object);
	}

	private void register(Object instance, Key key, boolean activate)
	{
		store.getEncoder(instance.getClass()).setId(instance, key);
		cache.set(instance, key);
		if (activate)
			cache.activate(instance);
	}

	private List<Entity> toEntities(Object object, Object longOrStringId, Object parent)
	{
		final List<Entity> result = Lists.newArrayList();
		final ObjectEncoder encoder = store.getEncoder(object.getClass());

		final Key existingKey = cache.getKey(object);
		final Key key;
		if (existingKey == null)
		{
			if (longOrStringId == null)
			{
				longOrStringId = encoder.getId(object);
				if (longOrStringId == null)
					longOrStringId = encoder.getNextId(service);
			}

			if (parent == null)
				parent = encoder.getParent(object);

			key = createKey(cache.getKey(parent), typeToKind(object.getClass()), longOrStringId);
		}
		else
		{
			key = existingKey;
			if (!cache.isActivated(object))
				throw new IllegalArgumentException("Attempt to store unactivated instance!");
		}
		final Entity e = new Entity(key);

		register(object, key, true);

		encoder.encode(new InstanceStore()
		{
			@Override
			public Key toKey(Object instance, Object parent)
			{
				if (instance == null)
					return null;

				Key key = cache.getKey(instance);
				if (key == null)
				{
					result.addAll(toEntities(instance, null, parent));
					key = cache.getKey(instance);
				}
				return key;
			}
		}, object, new PropertyStore()
		{
			@Override
			public void setProperty(String key, Object value, boolean index)
			{
				if (index)
					e.setProperty(key, value);
				else
					e.setUnindexedProperty(key, value);
			}
		});

		result.add(e);

		return result;
	}

	private <T> T createShell(Key key)
	{
		Class<T> clazz = store.kindToType(key.getKind());
		return createShell(clazz, key);
	}

	private <T> T createShell(final Class<T> clazz, final Key key)
	{
		T object = ObjectUtils.newInstance(clazz);
		register(object, key, false);
		return object;
	}

	private static final Function<RefreshEntry, Key> cKeyFunction = new Function<RefreshEntry, Key>()
	{
		@Override
		public Key apply(RefreshEntry input)
		{
			return input.key;
		}
	};

	private static class RefreshEntry
	{
		final Key key;
		final Object object;
		final List<String> path;

		public RefreshEntry(Key key, Object object, List<String> path)
		{
			super();
			this.key = key;
			this.object = object;
			this.path = path;
		}
	}

	private <T> T toObject(Class<T> clazz, final Entity entity, boolean refresh, int retries, ActivationFilter activationFilter)
	{
		final List<String> activationPath = Lists.newArrayList();
		Key key = entity.getKey();
		T object = cache.<T> getObject(key);
		if (object == null || refresh || !cache.isActivated(object))
		{
			if (object == null)
				object = createShell(clazz, key);
			else
				store.getEncoder(clazz).setId(object, key); // just set @Id which createShell does

			List<RefreshEntry> refreshList = Lists.newArrayList();

			refreshObject(object, entity, refreshList, activationPath, activationFilter);

			while (refreshList.size() > 0)
			{
				List<RefreshEntry> next = Lists.newArrayList();
				int retry = 0;
				while (refreshList.size() > 0 && retry <= retries)
				{
					ObjectUtils.sleep(retry * 200);
					Iterable<Key> keys = Iterables.transform(refreshList, cKeyFunction);
					Map<Key, Entity> entityMap = FutureUtils.safeGet(service.get(keys));
					List<RefreshEntry> retryPairs = Lists.newArrayList();
					for (RefreshEntry entry : refreshList)
					{
						Entity e = entityMap.get(entry.key);
						if (e == null)
							retryPairs.add(entry);
						else
							refreshObject(entry.object, e, next, entry.path, activationFilter);
					}
					refreshList = retryPairs;
					retry++;
				}
				if (refreshList.size() > 0)
					throw new KeysNotFoundException(Iterables.transform(refreshList, cKeyFunction));
				refreshList = next;
			}
		}
		else
		{
			store.getEncoder(clazz).setId(object, key); // just set @Id which createShell does - handle user-blammed ids
		}
		return object;
	}

	private <T> T refreshObject(T object, final Entity entity, final List<RefreshEntry> refreshList, final List<String> activationPath, final ActivationFilter activationFilter)
	{
		ObjectEncoder mapper = store.getEncoder(object.getClass());

		mapper.decode(new InstanceSource()
		{
			@Override
			public Object fromKey(String fieldName, Key key)
			{
				if (key == null)
					return null;

				Object o = cache.getObject(key);
				if (o == null)
				{
					o = createShell(key);
					if (activationFilter != null)
					{
						activationPath.add(fieldName);
						if (activationFilter.accept(activationPath))
							refreshList.add(new RefreshEntry(key, o, Lists.newArrayList(activationPath)));
						activationPath.remove(activationPath.size() - 1);
					}
					else
					{
						refreshList.add(new RefreshEntry(key, o, null));
					}
				}

				// TODO: ensure o is activated to the extent of the filter...
				return o;
			}

			@Override
			public Key getCurrentKey()
			{
				return entity.getKey();
			}
		}, object, new PropertySource()
		{
			@Override
			public <X> X getProperty(String key)
			{
				return ObjectUtils.<X> cast(entity.getProperty(key));
			}

			@Override
			public Collection<String> getKeys()
			{
				return entity.getProperties().keySet();
			}
		});
		cache.activate(object);
		return object;
	}

	private String typeToKind(Class<?> clazz)
	{
		return store.typeToKind(clazz);
	}

	private <T> Class<T> kindToType(String kind)
	{
		return store.kindToType(kind);
	}

	public Store.Multi store(final Iterable<?> instances)
	{
		return new Store.Multi()
		{
			Object parent;
			Iterable<?> ids = Collections.emptyList();

			public Store.Multi parent(Object parent)
			{
				this.parent = parent;
				return this;
			}

			public Store.Multi ids(Iterable<?> ids)
			{
				this.ids = ids;
				return this;
			}

			public Store.Multi ids(String... ids)
			{
				ids(Arrays.asList(ids));
				return this;
			}

			public Store.Multi ids(long... ids)
			{
				return ids(new LongIterable(ids));
			}

			public Store.Multi ids(Long... ids)
			{
				return ids(Arrays.asList(ids));
			}

			private List<Entity> getEntities()
			{
				List<Entity> entities = Lists.newArrayList();
				Iterator<?> idIterator = PaddedIterator.pad(ids.iterator());
				for (Object o : instances)
					entities.addAll(toEntities(o, idIterator.next(), parent));
				return entities;
			}

			public List<Key> now()
			{
				return FutureUtils.safeGet(later());
			}

			public Future<List<Key>> later()
			{
				final Future<List<Key>> future = service.put(getEntities());
				return new FutureWrapper<List<Key>>(future)
				{
					public List<Key> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
					{
						return future.get(timeout, unit);
					}
				};
			}
		};
	}

	public Store.Multi store(Object... instances)
	{
		return store(Arrays.asList(instances));
	}

	public Store.Single store(final Object instance)
	{
		return new Store.Single()
		{
			Object parent;
			Object id;

			public Store.Single parent(Object parent)
			{
				this.parent = parent;
				return this;
			}

			public Store.Single id(long id)
			{
				this.id = id;
				return this;
			}

			public Store.Single id(String id)
			{
				this.id = id;
				return this;
			}

			public Key now()
			{
				return FutureUtils.safeGet(later());
			}

			public Future<Key> later()
			{
				final Future<List<Key>> future = service.put(toEntities(instance, id, parent));
				return new FutureWrapper<Key>(future)
				{
					public Key get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
					{
						future.get(timeout, unit);
						return cache.getKey(instance);
					}
				};
			}
		};
	}

	private Key createKey(Key parentKey, String kind, final Object id)
	{
		if (id instanceof Number)
			return KeyFactory.createKey(parentKey, kind, ((Number) id).longValue());
		else
			return KeyFactory.createKey(parentKey, kind, id == null ? null : id.toString());
	}

	public <T> LoadById<T> load(final Class<T> type)
	{
		return new LoadById<T>()
		{
			public LoadById.Multi<T, Long> ids(final long... ids)
			{
				return ids(new LongIterable(ids));
			}

			@Override
			public Multi<T, Long> ids(Long... ids)
			{
				return ids(Arrays.asList(ids));
			}

			public LoadById.Multi<T, String> ids(String... ids)
			{
				return ids(Arrays.asList(ids));
			}

			public <I> LoadById.Multi<T, I> ids(final Iterable<I> ids)
			{
				return new AbstractMultiLoader<T, I>()
				{
					protected Object parent = null;

					public Multi<T, I> parent(Object parent)
					{
						this.parent = parent;
						return this;
					}

					@Override
					protected Iterable<Key> keys()
					{
						List<Key> keys = Lists.newArrayList();
						Key parentKey = cache.getKey(parent);
						String kind = typeToKind(type);
						for (Object id : ids)
							keys.add(createKey(parentKey, kind, id));
						return keys;
					}
				};
			}

			private LoadById.Single<T> _id(final Object id)
			{
				return new AbstractSingleLoader<T>()
				{
					protected Object parent = null;

					public LoadById.Single<T> parent(Object parent)
					{
						this.parent = parent;
						return this;
					}

					@Override
					protected Key key()
					{
						return createKey(cache.getKey(parent), typeToKind(type), id);
					}
				};
			}

			public LoadById.Single<T> id(long id)
			{
				return _id(id);
			}

			public LoadById.Single<T> id(String id)
			{
				return _id(id);
			}
		};
	}

	private abstract class AbstractSingleLoader<T> extends SimpleTypedSingleLoader<T, LoadById.Single<T>> implements LoadById.Single<T>
	{
	}

	private abstract class AbstractMultiLoader<T, I> extends SimpleTypedMultiLoader<T, LoadById.Multi<T, I>> implements LoadById.Multi<T, I>
	{
	}

	private abstract class AbstractKeyLoader extends SimpleSingleLoader<LoadByKey.Single> implements LoadByKey.Single
	{
		@Override
		public <T> T now()
		{
			return FutureUtils.safeGet(this.<T> later());
		}

		@Override
		public <T> Future<T> later()
		{
			return ObjectUtils.<Future<T>> cast(fetchSingle());
		}
	}

	private abstract class AbstractMultiKeyLoader extends SimpleLoader<LoadByKey.Multi> implements LoadByKey.Multi
	{
		@Override
		public <T> Iterable<T> now()
		{
			return FutureUtils.safeGet(this.<T> later());
		}

		@Override
		public <T> Future<Iterable<T>> later()
		{
			return ObjectUtils.<Future<Iterable<T>>> cast(fetch());
		}
	}

	abstract class SimpleLoader<X>
	{
		protected boolean activated = true;
		protected boolean refresh = false;
		protected int retries = 5;
		protected List<String> activatePatterns = Lists.newArrayList();

		public X refresh()
		{
			refresh = true;
			return ObjectUtils.<X> cast(this);
		}

		public X unactivated()
		{
			activated = false;
			return ObjectUtils.<X> cast(this);
		}

		public X retries(int count)
		{
			this.retries = count;
			return ObjectUtils.<X> cast(this);
		}

		public X activate(int levels)
		{
			return activate(ActivationFilter.getLevelActivationFilter(levels));
		}

		public X activate(String... patterns)
		{
			activatePatterns.addAll(Arrays.asList(patterns));
			return ObjectUtils.<X> cast(this);
		}

		protected abstract Iterable<Key> keys();

		protected <T> Future<List<T>> fetch()
		{
			if (activated)
			{
				final List<T> result = Lists.newArrayList();
				final List<Key> keys = Lists.newArrayList(keys());
				if (!refresh)
				{
					final Iterator<Key> iter = keys.iterator();
					while (iter.hasNext())
					{
						T object = cache.<T> getObject(iter.next());
						result.add(object);
						if (object != null)
							iter.remove();
					}
				}

				if (keys.size() == 0)
				{
					return new ImmediateFuture<List<T>>()
					{
						@Override
						public List<T> get()
						{
							return result;
						}
					};
				}
				else
				{
					final Future<Map<Key, Entity>> future = service.get(keys);
					return new FutureWrapper<List<T>>(future)
					{
						public List<T> get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
						{
							List<Key> currentKeys = keys;
							Map<Key, Entity> map = future.get(timeout, unit);
							int retry = 0;
							while (currentKeys.size() > 0 && retry <= retries)
							{
								List<Key> retryKeys = Lists.newArrayList();
								int offset = 0;
								final ActivationFilter filter = ActivationFilter.compile(activatePatterns);
								for (Key key : currentKeys)
								{
									Entity entity = map.get(key);
									if (entity == null)
									{
										retryKeys.add(key);
										if (offset >= result.size())
											result.add(null);
										offset++;
									}
									else
									{
										Class<T> clazz = kindToType(key.getKind());
										T object = entity == null ? null : ObjectStoreSessionImpl.this.<T> toObject(clazz, entity, refresh, retries, filter);
										while (offset < result.size() && result.get(offset) != null)
											offset++;
										if (offset < result.size())
											result.set(offset, object);
										else
											result.add(object);
										offset++;
									}
								}
								retry++;
								currentKeys = retryKeys;
								if (retry <= retries && currentKeys.size() > 0)
								{
									ObjectUtils.sleep(retry * 200);
									map = FutureUtils.safeGet(service.get(currentKeys));
								}
							}
							if (currentKeys.size() > 0)
								throw new KeysNotFoundException(currentKeys);
							return result;
						}
					};
				}
			}
			else
			{
				final List<T> result = Lists.newArrayList();
				for (Key key : keys())
				{
					T o = cache.<T> getObject(key);
					if (o == null)
						o = ObjectStoreSessionImpl.this.<T> createShell(key);
					result.add(o);
				}
				return new ImmediateFuture<List<T>>()
				{
					@Override
					public List<T> get()
					{
						return result;
					}
				};
			}
		}
	}

	abstract class SimpleSingleLoader<X> extends SimpleLoader<X>
	{
		protected abstract Key key();

		@Override
		protected Iterable<Key> keys()
		{
			return Collections.singletonList(key());
		}

		protected <T> Future<T> fetchSingle()
		{
			final Future<List<T>> future = fetch();
			return new FutureWrapper<T>(future)
			{
				@Override
				public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
				{
					List<T> list = future.get(timeout, unit);
					if (list.size() == 0)
						return null;
					else
						return list.get(0);
				}
			};
		}
	}

	abstract class SimpleTypedSingleLoader<T, X> extends SimpleSingleLoader<X> implements Terminator<T>
	{
		public T now()
		{
			return FutureUtils.safeGet(later());
		}

		public Future<T> later()
		{
			return this.<T> fetchSingle();
		}
	}

	abstract class SimpleTypedMultiLoader<T, X> extends SimpleLoader<X> implements Terminator<List<T>>
	{
		@Override
		public List<T> now()
		{
			return FutureUtils.safeGet(later());
		}

		@Override
		public Future<List<T>> later()
		{
			return ObjectUtils.cast(fetch());
		}
	}

	public LoadByKey.Single load(final Key key)
	{
		return new AbstractKeyLoader()
		{
			@Override
			protected Key key()
			{
				return key;
			}
		};
	}

	public LoadByKey.Multi load(final Key... keys)
	{
		return load(Arrays.asList(keys));
	}

	public LoadByKey.Multi load(final Iterable<Key> keys)
	{
		return new AbstractMultiKeyLoader()
		{
			@Override
			protected Iterable<Key> keys()
			{
				return keys;
			}
		};
	}
	private abstract class RefreshSingleImpl<T> extends SimpleTypedSingleLoader<T, Refresh<T>> implements Refresh<T>
	{
		{
			refresh = true;
		}
	}

	public <T> Refresh<T> refresh(final T object)
	{
		return new RefreshSingleImpl<T>()
		{
			@Override
			protected Key key()
			{
				return cache.getKey(object);
			}
		};
	}

	public <T> Refresh<List<T>> refresh(final T... objects)
	{
		return refresh(Arrays.asList(objects));
	}

	private abstract class RefreshMultiImpl<T> extends SimpleTypedMultiLoader<T, Refresh<List<T>>> implements Refresh<List<T>>
	{
		{
			refresh = true;
		}
	}

	public <T> Refresh<List<T>> refresh(final Iterable<T> objects)
	{
		return new RefreshMultiImpl<T>()
		{
			@Override
			protected Iterable<Key> keys()
			{
				List<Key> keys = Lists.newArrayList();
				for (Object object : objects)
					keys.add(cache.getKey(object));
				return keys;
			}
		};
	}

	public <T> Find<T> find(final Class<T> type)
	{
		return new Find<T>()
		{
			final Query query = new Query(store.typeToKind(type));

			@Override
			public Find<T> sort(String field)
			{
				return sort(field, Find.Sort.Ascending);
			}

			@Override
			public Find<T> sort(String field, Find.Sort direction)
			{
				SortDirection dir;

				switch (direction)
				{
					case Ascending :
						dir = SortDirection.ASCENDING;
						break;
					default :
						dir = SortDirection.DESCENDING;
				}
				query.addSort(field, dir);
				return this;
			}

			@Override
			public Find<T> filter(String field, Object value)
			{
				return filter(field, Find.Op.EqualTo, value);
			}

			@Override
			public Find<T> filterIn(String field, Iterable<?> values)
			{
				return _filter(field, FilterOperator.IN, values);
			}

			@Override
			public Find<T> filter(String field, Find.Op op, Object value)
			{
				FilterOperator gop;
				switch (op)
				{
					case LessThan :
						gop = FilterOperator.LESS_THAN;
						break;
					case LessThanOrEqualTo :
						gop = FilterOperator.LESS_THAN_OR_EQUAL;
						break;
					case GreaterThanOrEqualTo :
						gop = FilterOperator.GREATER_THAN_OR_EQUAL;
						break;
					case GreaterThan :
						gop = FilterOperator.GREATER_THAN;
						break;
					case NotEqualTo :
						gop = FilterOperator.NOT_EQUAL;
						break;
					default :
					case EqualTo :
						gop = FilterOperator.EQUAL;
						break;
				}
				return _filter(field, gop, value);
			}

			private Find<T> _filter(String field, FilterOperator gop, Object value)
			{
				ObjectEncoder encoder = store.getEncoder(type);
				String[] path = field.split("\\.");
				Field f = encoder.getField(path);
				if (f == null)
					throw new IllegalArgumentException("No such field " + field + " on class " + type);

				Parent parentAnno = f.getAnnotation(Parent.class);
				if (parentAnno != null)
				{
					switch (parentAnno.value())
					{
						case AncestorQuery :
						{
							if (gop == FilterOperator.EQUAL)
								return ancestor(value);
							else
								throw new IllegalArgumentException("Cannot filter on parent-key encoded field " + field + " in class " + type + " using " + gop);
						}
						case NoFilter :
						{
							throw new IllegalArgumentException("No filtering on FilterPolicy." + parentAnno.value() + " parent relationships.");
						}
						case RetainKey :
							// all good in this case
					}
				}
				FieldEncoder fieldEncoder = encoder.getEncoder(path);
				if (fieldEncoder instanceof CollectionEncoder)
					fieldEncoder = ((CollectionEncoder) fieldEncoder).getDefaultEncoder();
				
				final boolean isMultiple;
				if(!(value instanceof Iterable))
				{
					value = Collections.singleton(value);
					isMultiple = false;
				}
				else
				{
					isMultiple = true;
				}
				
				
				final Map<String, Object> properties = Maps.newHashMap();
				for(Object v : (Iterable<?>)value)
				fieldEncoder.encode(new InstanceStore()
				{
					@Override
					public Key toKey(Object instance, Object parent)
					{
						Key key = cache.getKey(instance);
						if (key == null)
							throw new IllegalArgumentException("Must filter on instances from same ObjectStoreSession");
						return key;
					}
				}, ConvertUtils.convert(v, f.getType()), new PropertyStore()
				{
					@Override
					public void setProperty(String key, Object value, boolean index)
					{
						if(isMultiple)
						{
							List<Object> list = ObjectUtils.cast(properties.get(key));
							if(list == null)
							{
								list = Lists.newArrayList();
								properties.put(key, list);
							}
							list.add(value);
						}
						else
						{
							properties.put(key, value);
						}
					}
				});

				if (properties.size() > 1 && gop != FilterOperator.EQUAL)
					throw new IllegalArgumentException("Cannot filter embedded types with operator: " + gop);

				for (Entry<String, Object> entry : properties.entrySet())
					query.addFilter(entry.getKey(), gop, entry.getValue());

				return this;
			}

			@Override
			public Find<T> filterBetween(String field, Object lower, Object upper)
			{
				return filterBetween(field, lower, upper, true, false);
			}

			@Override
			public Find<T> filterBetween(String field, Object lower, Object upper, boolean includeBottom, boolean includeTop)
			{
				Find.Op lowerOp, upperOp;
				if (includeBottom)
					lowerOp = Find.Op.GreaterThanOrEqualTo;
				else
					lowerOp = Find.Op.GreaterThan;

				if (includeTop)
					upperOp = Find.Op.LessThanOrEqualTo;
				else
					upperOp = Find.Op.LessThan;

				filter(field, lowerOp, lower);
				filter(field, upperOp, upper);
				return this;
			}

			@Override
			public Find<T> filterBeginsWith(String field, String value)
			{
				filterBetween(field, value, value + "\uFFFD");
				return this;
			}

			@Override
			public Find<T> ancestor(Object ancestor)
			{
				query.setAncestor(cache.getKey(ancestor));
				return this;
			}

			int start = 0;

			@Override
			public Find<T> start(int offset)
			{
				this.start = offset;
				return this;
			}

			int limit = Integer.MAX_VALUE;

			@Override
			public Find<T> limit(int count)
			{
				limit = count;
				return this;
			}

			private boolean activated = true;

			@Override
			public Find<T> unactivated()
			{
				activated = false;
				query.setKeysOnly();
				return this;
			}

			private boolean refresh = false;

			@Override
			public Find<T> refresh()
			{
				refresh = true;
				return this;
			}

			private int retries = 5;

			@Override
			public Find<T> retries(int count)
			{
				retries = count;
				return this;
			}

			private List<String> activatePatterns = Lists.newArrayList();

			@Override
			public Find<T> activate(String... patterns)
			{
				activatePatterns.addAll(Arrays.asList(patterns));
				return this;
			}

			@Override
			public Find<T> activate(int levels)
			{
				return activate(ActivationFilter.getLevelActivationFilter(levels));
			}

			@Override
			public Iterable<T> now()
			{
				return FutureUtils.safeGet(later());
			}

			@Override
			public Future<Iterable<T>> later()
			{
				final Iterable<T> result = queryNow(); // get the query going now
				return new ImmediateFuture<Iterable<T>>()
				{
					@Override
					public Iterable<T> get()
					{
						return result;
					}
				};
			}

			private Iterable<T> queryNow()
			{
				return toEntityIterable(service.prepare(query).asIterable(withOffset(start).limit(limit)));
			}

			private Iterable<T> toEntityIterable(final Iterable<Entity> iterable)
			{
				return new Iterable<T>()
				{
					@Override
					public Iterator<T> iterator()
					{
						return new Iterator<T>()
						{
							Iterator<Entity> nested = iterable.iterator();
							ActivationFilter filter = ActivationFilter.compile(activatePatterns);

							@Override
							public boolean hasNext()
							{
								return nested.hasNext();
							}

							@SuppressWarnings("unchecked")
							@Override
							public T next()
							{
								Entity entity = nested.next();
								if (activated)
								{
									return toObject(type, entity, refresh, retries, filter);
								}
								else
								{
									Key key = entity.getKey();
									T object = (T) cache.getKey(key);
									if (object == null)
										object = createShell(type, key);
									return object;
								}
							}

							@Override
							public void remove()
							{
								throw new UnsupportedOperationException();
							}
						};
					}
				};
			}

			@Override
			public Terminator<T> single()
			{
				limit(1);
				return new AbstractTerminator<T>()
				{
					@Override
					public Future<T> later()
					{
						final Iterable<T> result = queryNow();
						return new ImmediateFuture<T>()
						{
							@Override
							public T get()
							{
								Iterator<T> iterator = result.iterator();
								if (iterator.hasNext())
									return iterator.next();
								else
									return null;
							}
						};
					}
				};
			}

			@Override
			public Terminator<Integer> count()
			{
				return new AbstractTerminator<Integer>()
				{
					@Override
					public Future<Integer> later()
					{
						return new ImmediateFuture<Integer>()
						{
							@Override
							public Integer get()
							{
								return service.prepare(query).countEntities(FetchOptions.Builder.withDefaults());
							}
						};
					}
				};
			}
		};
	}

	@Override
	public Delete delete(Object o)
	{
		return delete(Collections.singleton(o));
	}

	@Override
	public Delete delete(Object... os)
	{
		return delete(Arrays.asList(os));
	}

	@Override
	public Delete delete(final Iterable<?> os)
	{
		return new Delete()
		{
			@Override
			public Void now()
			{
				return FutureUtils.safeGet(later());
			}

			@Override
			public Future<Void> later()
			{
				final Iterable<Key> keys = Iterables.transform(os, new Function<Object, Key>()
				{
					@Override
					public Key apply(Object input)
					{
						return cache.getKey(input);
					}
				});
				final Future<Void> future = service.delete(keys);

				return new FutureWrapper<Void>(future)
				{
					@Override
					public Void get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException
					{
						future.get(timeout, unit);
						for (Key key : keys)
							cache.remove(key);
						return null;
					}
				};
			}
		};
	}

	@Override
	public <T> Terminator<Void> delete(Class<T> clazz)
	{
		return delete(find(clazz).unactivated().now());
	}
}
