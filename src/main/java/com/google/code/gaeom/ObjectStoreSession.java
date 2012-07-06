package com.google.code.gaeom;

import java.util.List;

import com.google.appengine.api.datastore.Key;

/**
 * The main point of interaction with the framework, ObjectStoreSession is typically scoped at the request level. Within
 * an ObjectStoreSession, object identity will be guaranteed consistent. That is, if you retrieve an object and then, by
 * virtue of a future query retrieve the same object, the framework will return the same instance rather than a
 * duplicate copy. Each ObjectStoreSession is associated with an ObjectStore that caches all of the mapping metadata and
 * the {@link com.google.code.gaeom.annotation.Cached} annotated instances.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface ObjectStoreSession
{
	/**
	 * @return the ObjectStore with which this ObjectStoreSession is associated
	 */
	public ObjectStore getObjectStore();

	/**
	 * Retrieves a key for an already persistent instance.
	 * 
	 * @param object
	 *            the object for which the Key is desired.
	 * @return the Key assocated with {@code object} or null if the object is not associated with this session
	 */
	public Key getKey(Object object);

	/**
	 * Begins a {@link Store.Multi} command to store the {@code objects}. The Store commmands are used to initially
	 * persist objects as well as to update already persistent objects. No persistence will be performed until you
	 * invoke the now() or later() methods to specify synchronous or asynchronous operation.
	 * 
	 * @param objects
	 *            the objects to store
	 * @return the {@link Store.Multi} command instance
	 */
	public Store.Multi store(Iterable<?> objects);

	/**
	 * See {@link #store(Iterable)}
	 * 
	 * @param objects
	 *            the objects to store
	 * @return the {@link Store.Multi} command instance
	 */
	public Store.Multi store(Object... objects);

	/**
	 * Begins a {@link Store.Single} command to store the {@code object}. Store commands are use to initially persist
	 * instances as well as to update already persistent instances. No persistence will be perform until you invoke the
	 * now() or later() methods to specify synchronous or asynchronous operation.
	 * 
	 * @param object
	 *            the object to persist
	 * @return the {@link Store.Single} command instance
	 */
	public Store.Single store(Object object);

	/**
	 * Begins a {@link LoadById} command to load instances of {@code type}. The load command must be terminated by a
	 * call to now() or later() to specify synchronous or asynchronous operation. You will need to specify the id values
	 * (not keys) to the command before terminating.
	 * 
	 * @param type
	 *            the class of the instances you want to load
	 * @return the {@link LoadById} command instance
	 */
	public <T> LoadById<T> load(Class<T> type);

	/**
	 * Begins a {@link LoadByKey.Single} command to load a single instance from its Key. The load command must be
	 * terminated by a call to now() or later() to specify synchronous or asynchronous operation.
	 * 
	 * @param key
	 *            the Key of the object you with to load
	 * @return the {@link LoadByKey.Single} command instance
	 */
	public LoadByKey.Single load(Key key);

	/**
	 * Begins a {@link LoadByKey.Multi} command to load more than one object from their Key instances. The load command
	 * must be terminated by a call to now() or later() to specify synchronouse or asynchrnous operation.
	 * 
	 * @param keys
	 *            the keys to load
	 * @return a {@link LoadByKey.Multi} command instance
	 */
	public LoadByKey.Multi load(Iterable<Key> keys);

	/**
	 * See {@link #load(Iterable)}
	 * 
	 * @param keys
	 *            the keys to load
	 * @return a {@link LoadByKey.Multi} command instance
	 */
	public LoadByKey.Multi load(Key... keys);

	/**
	 * Begins a {@link Find} command to query for instances of {@code type}.
	 * 
	 * @param type
	 *            the class whose instances you wish to query for
	 * @return a {@link Find} command instance
	 */
	public <T> Find<T> find(Class<T> type);

	/**
	 * Begins a {@link Refresh} command to refresh the {@code object} with the latest available data from the datastore.
	 * The refresh command must be terminated by a call to now() or later() to specify synchronous or asynchronous
	 * operation.
	 * 
	 * @param object
	 *            the object to refresh
	 * @return a {@link Refresh} command instance
	 */
	public <T> Refresh<T> refresh(T object);

	/**
	 * See {@link #refresh(Iterable)}
	 * 
	 * @param objects
	 *            the objects to refresh
	 * @return a {@link Refresh} command instance
	 */
	public <T> Refresh<List<T>> refresh(T... objects);

	/**
	 * Checks to see if the object has been activated in this session. If the object hasn't been activated, only its @Id
	 * field (if it has one) is populated. Call refresh(object).now() to activate it.
	 * 
	 * @param object
	 * @return true iff object has been activated
	 */
	public boolean isActivated(Object object);

	/**
	 * Begins a {@link Refresh} command to refresh the {@code objects} with the latest available data from the
	 * datastore. The refresh command must be terminated by a call to now() or later() to specify synchronous or
	 * asynchronous operation.
	 * 
	 * @param objects
	 *            the objects to refresh
	 * @return a {@link Refresh} command instance
	 */
	public <T> Refresh<List<T>> refresh(Iterable<T> objects);

	/**
	 * Begins a {@link Delete} command to delete the data associated with {@code object} from the datastore. The delete
	 * command must be terminated by a call to now() or later() to specify synchronous or asynchronous operation.
	 * 
	 * @param object
	 *            the object to delete
	 * @return the {@link Delete} command instance
	 */
	public Delete delete(Object object);

	/**
	 * See {@link #delete(Iterable)}
	 * 
	 * @param objects
	 *            the objects to delete
	 * @return the {@link Delete} command instance
	 */
	public Delete delete(Object... objects);

	/**
	 * Begins a {@link Delete} command to delete the data associated with {@code objects} from the datastore. The delete
	 * command must be terminated by a call to now() or later() to specify synchronous or asynchronous operation.
	 * 
	 * @param objects
	 *            the objects to delete
	 * @return the {@link Delete} command instance
	 */
	public Delete delete(Iterable<?> objects);

	/**
	 * Begins a delete command to delete all instances of {@code clazz} from the datastore. The delete command must be
	 * terminated by a call to now() or later() to specify synchronous or asynchronous operation.
	 * 
	 * @param clazz
	 * @return
	 */
	public <T> Terminator<Void> delete(Class<T> clazz);
}
