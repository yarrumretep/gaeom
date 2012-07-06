package com.google.code.gaeom;

/**
 * Specifies the interface for custom type encoders. See {@link com.google.code.gaeom.annotation.EncodeWith} annotation. This
 * type of encoder will encode an instance of {@code D} into a single value for storage in the datastore. For more
 * complex encoders that require multiple values be stored, see {@link CustomMultiEncoder}
 * 
 * Implementation instances should be stateless and thread-safe.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 * 
 * @param <D>
 *            decoded type
 * @param <E>
 *            encoded type, must be compatible with GAE storage types. See:
 *            http://code.google.com/appengine/docs/java/datastore/entities.html#Properties_and_Value_Types
 */
public interface CustomEncoder<D, E>
{
	/**
	 * Invoked to encode an instance of {@code D} for persistence in the datastore.
	 * 
	 * @param value
	 *            the value to encode
	 * @return the value to store in the datastore
	 */
	public E encode(D value);

	/**
	 * Invoked to decode the datastore-stored value into an instance of {@code D}.
	 * 
	 * @param value
	 *            the value that was stored in the datastore
	 * @return the corresponding decoded {@code D} value.
	 */
	public D decode(E value);
}
