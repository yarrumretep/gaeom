package com.google.code.gaeom;

import java.util.List;

/**
 * The command to load objects by type and id values - {@link ObjectStoreSession#load(Class)}. This is used when you know
 * the id values of specific objects and want to access them rapidly without a full query.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface LoadById<T>
{
	/**
	 * Specifies the {@code long} id values to load
	 * 
	 * @param ids
	 *            the id values of the objects to load.
	 * @return a LoadById.Multi instance to load a group of objects.
	 */
	Multi<T, Long> ids(long... ids);

	/**
	 * {@link #ids(long...)}
	 * 
	 * @param ids
	 *            the id values of the objects to load.
	 * @return a LoadById.Multi command instance to load a group of objects.
	 */
	Multi<T, Long> ids(Long... ids);

	/**
	 * Specifies the {@code String} id values to load
	 * 
	 * @param ids
	 *            the id values of the objects to load.
	 * @return a LoadById.Multi command instance to load a group of objects.
	 */
	Multi<T, String> ids(String... ids);

	/**
	 * Specifies the {@code String} or {@code Long} id values to load
	 * 
	 * @param ids
	 *            the id values of the objects to load
	 * @return a LoadByid.Multi command instance to load a group of objects;
	 */
	<I> Multi<T, I> ids(Iterable<I> ids);

	/**
	 * Specifies the {@code long} id value of the object to load.
	 * 
	 * @param id
	 *            the id value of the object
	 * @return a Single command instance to load a single object
	 */
	Single<T> id(long id);

	/**
	 * Specifies the {@code String} id value of the object to load.
	 * 
	 * @param id
	 *            the id value of the object
	 * @return a Single command instance to load a single object
	 */
	Single<T> id(String id);

	interface Common<C extends Common<C>> extends Load<C>
	{
		/**
		 * Specifies the parent object for the load command. Keys to fetch for the results will be parented by the key
		 * of the {@code parent}
		 * 
		 * @param parent
		 *            the parent object
		 * @return the command instance
		 */
		C parent(Object parent);
	}

	/**
	 * Interface to load multiple instances
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Multi<T, I> extends Common<Multi<T, I>>, Terminator<List<T>>
	{
	}

	/**
	 * Interface to load a single instance
	 * 
	 * @author Peter Murray <gaeom@pmurray.com>
	 */
	interface Single<T> extends Common<Single<T>>, Terminator<T>
	{
	}
}
