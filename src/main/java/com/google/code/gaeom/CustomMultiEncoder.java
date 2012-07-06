package com.google.code.gaeom;

import java.util.Map;

/**
 * Specifies the interface for a complex encoder that requires multiple values to be stored per encoded instance. See
 * {@link com.google.code.gaeom.annotation.EncodeWith}.
 * 
 * Implementation instances must be stateless and thread safe.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
public interface CustomMultiEncoder<D> extends CustomEncoder<D, Map<String, ?>>
{
	/**
	 * Invoked to encode a value of {@code D} into properties for storage in the datastore
	 * 
	 * @param value
	 *            the value to be encoded
	 * @return a Map instance with String keys containing the datastore compatible values to be stored.
	 */
	public Map<String, ?> encode(D value);

	/**
	 * Invoked to decode the stored values into an instance of {@code D}.
	 * 
	 * @param value
	 *            the map of values stored previously in the datastore
	 * @return the corresponding instance of {@code D} that results from the values.
	 */
	public D decode(Map<String, ?> value);
}
