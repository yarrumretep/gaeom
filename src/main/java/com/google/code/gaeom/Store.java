package com.google.code.gaeom;

import java.util.List;

import com.google.appengine.api.datastore.Key;

/**
 * The interface for the Store command. {@link ObjectStoreSession#store(Iterable)}
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Store
{
	/**
	 * Common functionality for all store commands
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Common<C extends Common<C>>
	{
		/**
		 * Specifies the parent object for this store command. All objects being stored in this command will have their
		 * Keys parented by the key of the {@code parent} object.
		 * 
		 * @param parent
		 *            the parent object
		 * @return the command instance
		 */
		C parent(Object parent);
	}

	/**
	 * Store command for storing multiple objects
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Multi extends Common<Multi>, Terminator<List<Key>>
	{
		/**
		 * Specify the id values for the instances being stored. There must be as many ids specified as there were
		 * objects being stored. This is optional as the datastore will assign ids otherwise.
		 * 
		 * @param ids
		 *            the id values to use
		 * @return the command instance
		 */
		Multi ids(Iterable<?> ids);

		/**
		 * Specify the id values for the instances being stored. There must be as many ids specified as there were
		 * objects being stored. This is optional as the datastore will assign ids otherwise.
		 * 
		 * @param ids
		 *            the id values to use
		 * @return the command instance
		 */
		Multi ids(String... ids);

		/**
		 * Specify the id values for the instances being stored. There must be as many ids specified as there were
		 * objects being stored. This is optional as the datastore will assign ids otherwise.
		 * 
		 * @param ids
		 *            the id values to use
		 * @return the command instance
		 */
		Multi ids(long... ids);

		/**
		 * Specify the id values for the instances being stored. There must be as many ids specified as there were
		 * objects being stored. This is optional as the datastore will assign ids otherwise.
		 * 
		 * @param ids
		 *            the id values to use
		 * @return the command instance
		 */
		Multi ids(Long... ids);
	}

	/**
	 * Interface for storing a single object
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Single extends Common<Single>, Terminator<Key>
	{
		/**
		 * Specify the id value for the instance being stored. This is optional as the datastore will assign an id
		 * otherwise.
		 * 
		 * @param id
		 *            the id value to use
		 * @return the command instance
		 */
		Single id(long id);

		/**
		 * Specify the id value for the instance being stored. This is optional as the datastore will assign an id
		 * otherwise.
		 * 
		 * @param id
		 *            the id value to use
		 * @return the command instance
		 */
		Single id(String id);
	}
}
