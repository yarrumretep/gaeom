package com.google.code.gaeom;

import com.google.appengine.api.datastore.AsyncDatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceConfig;
import com.google.code.gaeom.impl.ObjectStoreImpl;

/**
 * The ObjectStore represents the mapping between the application data objects and the appengine datastore. Meta-data
 * that represents the mapping is cached at this level as well as instances that are marked with the
 * {@link com.google.code.gaeom.annotation.Cached} annotation. Typical usage of this class is to create a single
 * instance for the JVM and re-use it. For convenience, you may decide to use the {@link Singleton} instance:
 * <p>
 * <blockquote>
 * 
 * <pre>
 * 
 * ObjectStoreSession oss = ObjectStore.Singleton.get().beginSession();
 * </pre>
 * 
 * </blockquote>
 * </p>
 * Most interaction with the system is performed with the {@link ObjectStoreSession} class.
 * 
 * Instances of this class also register classes you will be using with their kind-names in the datastore. Registration
 * is optional - the system will generate names based on the fully qualified class name for you. Registration must be
 * performed before the classes are accessed.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface ObjectStore
{
	/**
	 * Static factory for ObjectStore instances
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	public static class Factory
	{
		/**
		 * @return a new ObjectStore instance
		 */
		public static ObjectStore create()
		{
			return new ObjectStoreImpl();
		}
	}

	/**
	 * Convenience JVM scoped singleton factory for an ObjectStore instance
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	public static class Singleton
	{
		private static final ObjectStore singleton = Factory.create();

		/**
		 * @return a JVM scoped ObjectStore instance
		 */
		public static ObjectStore get()
		{
			return singleton;
		}
	}

	/**
	 * Registers a datastore kind name for a persistent class.
	 * 
	 * @param kind
	 *            the kind name to use
	 * @param type
	 *            the class to map to the {@code kind}
	 */
	void register(String kind, Class<?> type);

	/**
	 * Creates a new {@link ObjectStoreSession} with default settings. By default the DatastoreService is configured to
	 * use the EVENTUAL read consistency as the framework has automatic retry functionality built in.
	 * 
	 * @return a newly created {@link ObjectStoreSession}
	 */
	ObjectStoreSession beginSession();

	/**
	 * Creates a new {@link ObjectStoreSession} with the configuration given.
	 * 
	 * @param config
	 *            the datastore service configuration to use
	 * @return a newly created {@link ObjectStoreSession}
	 */
	ObjectStoreSession beginSession(DatastoreServiceConfig config);

	/**
	 * Creates a new {@link ObjectStoreSession} that uses the provided AsyncDatastoreService for datastore access.
	 * 
	 * @param service
	 *            the datastore access service to use
	 * @return a newly created {@link ObjectStoreSession}
	 */
	ObjectStoreSession beginSession(AsyncDatastoreService service);

	/**
	 * Clears the receiver's cache of objects marked with the {@link com.google.code.gaeom.annotation.Cached}
	 * annotation.
	 */
	void clearCache();
}
