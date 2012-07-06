package com.google.code.gaeom;

/**
 * Abstract Load interface used for both Single and Multi Load commands
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface Load<C extends Load<C>> extends LoadOrRefresh<C>
{
	/**
	 * Specify that the command should load unactivated object (keys only). This cuts down on data transfer and object
	 * hydration time, but results in objects that only have their {@link com.google.code.gaeom.annotation.Id} fields
	 * populated.
	 * 
	 * @return the command instance
	 */
	public C unactivated();

	/**
	 * Specify that the command should refresh objects that have already been loaded by re-fetching their data from the
	 * datastore. Otherwise, the load command will just return objects that the ObjectStoreSession already knows about
	 * without hitting the datastore.
	 * 
	 * @return the command instance
	 */
	public C refresh();
}