package com.google.code.gaeom;

/**
 * An abstract interface encapsulating methods for both Load commands and Refresh commands
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface LoadOrRefresh<C extends LoadOrRefresh<C>>
{

	/**
	 * Specifies how many times (with increasing delays between attempts) to try to retrieve keys that cannot be found.
	 * The default is 5.
	 * 
	 * @param count
	 *            the number of times to retry
	 * @return the command instance
	 */
	public C retries(int count);

	/**
	 * Specifies that the command should activate related objects based on matching the {@code patterns}. Relationships
	 * whose patterns do not match will not be activated. A relationship is activated if and only if at least one
	 * positive filter matches and no negative filters match. The default is "**"
	 * 
	 * @param patterns
	 *            e.g. "**" activates everything, "foo.**" activates the foo relationship and anything it refers too
	 *            fully, but nothing else, "*" indicates activate the immediate relationships, but no further, ""
	 *            indicates activate only the root object(s), no relationships, "*.*" indicated activate relationships
	 *            on the root object, and relationships one level deep. "!foo.*" indicates that the relationships within
	 *            the foo relationship should not be activated.
	 * @return the command instance
	 */
	public C activate(String... patterns);

	/**
	 * Specifies that the object graph should be activated to {@code levels} deep. This method generated and adds a
	 * pattern indicating the depth.
	 * 
	 * @param levels
	 *            the number of levels to activate. 0 means just the root objects themselves.
	 * @return the command instance
	 */
	public C activate(int levels);

}