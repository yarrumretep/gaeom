package com.google.code.gaeom.performance;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreAttributes;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.KeyRange;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
import com.google.appengine.repackaged.com.google.common.collect.Maps;
import com.google.code.gaeom.util.ImmediateFuture;

public class DummyDatastoreService implements DatastoreService
{
	Map<String, Entity> store = Maps.newHashMap();

	public Collection<Transaction> getActiveTransactions()
	{
		throw new UnsupportedOperationException();
	}

	public Transaction getCurrentTransaction()
	{
		throw new UnsupportedOperationException();
	}

	public Transaction getCurrentTransaction(Transaction arg0)
	{
		throw new UnsupportedOperationException();
	}

	public PreparedQuery prepare(Query arg0)
	{
		throw new UnsupportedOperationException();
	}

	public PreparedQuery prepare(Transaction arg0, Query arg1)
	{
		throw new UnsupportedOperationException();
	}

	public KeyRangeState allocateIdRange(KeyRange arg0)
	{
		throw new UnsupportedOperationException();
	}

	public KeyRange allocateIds(String arg0, long arg1)
	{
		long start = getNext(arg0);
		ids.put(arg0, start + arg1 - 1);
		return new KeyRange(null, arg0, start, start + arg1 - 1);
	}

	public KeyRange allocateIds(Key arg0, String arg1, long arg2)
	{
		throw new UnsupportedOperationException();
	}

	public Transaction beginTransaction()
	{
		return new Transaction()
		{

			public void commit()
			{
			}

			public Future<Void> commitAsync()
			{
				return null;
			}

			public String getApp()
			{
				return null;
			}

			public String getId()
			{
				return null;
			}

			public boolean isActive()
			{
				return false;
			}

			public void rollback()
			{
			}

			public Future<Void> rollbackAsync()
			{
				return null;
			}
		};
	}

	public void delete(Key... arg0)
	{
		delete(Arrays.asList(arg0));
	}

	public void delete(Iterable<Key> arg0)
	{
		delete(null, arg0);
	}

	public void delete(Transaction arg0, Key... arg1)
	{
		delete(arg0, Arrays.asList(arg1));
	}

	public void delete(Transaction arg0, Iterable<Key> arg1)
	{
		for (Key key : arg1)
			store.remove(key.toString());
	}

	public Entity get(Key arg0) throws EntityNotFoundException
	{
		return get(null, arg0);
	}

	public Map<Key, Entity> get(Iterable<Key> arg0)
	{
		return get(null, arg0);
	}

	public Entity get(Transaction arg0, Key arg1) throws EntityNotFoundException
	{
		Entity e = store.get(arg1.toString());
		if (e == null)
			throw new EntityNotFoundException(arg1);
		return e;
	}

	public Map<Key, Entity> get(Transaction arg0, Iterable<Key> arg1)
	{
		Map<Key, Entity> result = Maps.newHashMap();
		for (Key key : arg1)
			result.put(key, store.get(key.toString()));
		return result;
	}

	public DatastoreAttributes getDatastoreAttributes()
	{
		throw new UnsupportedOperationException();
	}

	public Key put(Entity arg0)
	{
		return put(null, arg0);
	}

	public List<Key> put(Iterable<Entity> arg0)
	{
		return put(null, arg0);
	}

	private Map<String, Long> ids = Maps.newHashMap();

	private long getNext(String kind)
	{
		Long value = ids.get(kind);
		if (value == null)
			value = 1L;
		else
			value = value + 1;
		ids.put(kind, value);
		return value;
	}

	public Key put(Transaction arg0, Entity arg1)
	{
		Key key = arg1.getKey();

		if (!key.isComplete())
			key = KeyFactory.createKey(key.getParent(), key.getKind(), getNext(key.getKind()));

		store.put(key.toString(), arg1);

		return key;
	}

	public List<Key> put(Transaction arg0, Iterable<Entity> arg1)
	{
		List<Key> result = Lists.newArrayList();
		for (Entity e : arg1)
			result.add(put(arg0, e));
		return result;
	}

	public AsyncDatastoreService asAsync()
	{
		return new AsyncDatastoreService()
		{
			public Collection<Transaction> getActiveTransactions()
			{
				return DummyDatastoreService.this.getActiveTransactions();
			}

			public Transaction getCurrentTransaction()
			{
				return DummyDatastoreService.this.getCurrentTransaction();
			}

			public Transaction getCurrentTransaction(Transaction arg0)
			{
				return DummyDatastoreService.this.getCurrentTransaction(arg0);
			}

			public PreparedQuery prepare(Query arg0)
			{
				return DummyDatastoreService.this.prepare(arg0);
			}

			public PreparedQuery prepare(Transaction arg0, Query arg1)
			{
				return DummyDatastoreService.this.prepare(arg0, arg1);
			}

			public Future<KeyRange> allocateIds(final String arg0, final long arg1)
			{
				return new ImmediateFuture<KeyRange>()
				{
					@Override
					public KeyRange get()
					{
						return DummyDatastoreService.this.allocateIds(arg0, arg1);
					}
				};
			}

			public Future<KeyRange> allocateIds(final Key arg0, final String arg1, final long arg2)
			{
				return new ImmediateFuture<KeyRange>()
				{
					@Override
					public KeyRange get()
					{
						return DummyDatastoreService.this.allocateIds(arg0, arg1, arg2);
					}
				};
			}

			public Future<Transaction> beginTransaction()
			{
				return new ImmediateFuture<Transaction>()
				{
					@Override
					public Transaction get()
					{
						return DummyDatastoreService.this.beginTransaction();
					}
				};
			}

			public Future<Void> delete(Key... arg0)
			{
				return delete(Arrays.asList(arg0));
			}

			public Future<Void> delete(Iterable<Key> arg0)
			{
				return delete(null, arg0);
			}

			public Future<Void> delete(Transaction arg0, Key... arg1)
			{
				return delete(arg0, Arrays.asList(arg1));
			}

			public Future<Void> delete(final Transaction arg0, final Iterable<Key> arg1)
			{
				return new ImmediateFuture<Void>()
				{
					@Override
					public Void get()
					{
						DummyDatastoreService.this.delete(arg0, arg1);
						return null;
					}
				};
			}

			public Future<Entity> get(Key arg0)
			{
				return get(null, arg0);
			}

			public Future<Map<Key, Entity>> get(Iterable<Key> arg0)
			{
				return get(null, arg0);
			}

			public Future<Entity> get(final Transaction arg0, final Key arg1)
			{
				return new ImmediateFuture<Entity>()
				{
					@Override
					public Entity get()
					{
						try
						{
							return DummyDatastoreService.this.get(arg0, arg1);
						}
						catch (EntityNotFoundException e)
						{
							return null;
						}
					}
				};
			}

			public Future<Map<Key, Entity>> get(final Transaction arg0, final Iterable<Key> arg1)
			{
				return new ImmediateFuture<Map<Key, Entity>>()
				{
					@Override
					public Map<Key, Entity> get()
					{
						return DummyDatastoreService.this.get(arg0, arg1);
					}
				};
			}

			public Future<Key> put(Entity arg0)
			{
				return put(null, arg0);
			}

			public Future<List<Key>> put(Iterable<Entity> arg0)
			{
				return put(null, arg0);
			}

			public Future<Key> put(final Transaction arg0, final Entity arg1)
			{
				return new ImmediateFuture<Key>()
				{
					@Override
					public Key get()
					{
						return DummyDatastoreService.this.put(arg0, arg1);
					}
				};
			}

			public Future<List<Key>> put(final Transaction arg0, final Iterable<Entity> arg1)
			{
				return new ImmediateFuture<List<Key>>()
				{
					@Override
					public List<Key> get()
					{
						return DummyDatastoreService.this.put(arg0, arg1);
					}
				};
			}
		};
	}

	public void clear()
	{
		store.clear();
		ids.clear();
	}
}
