package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that instances of the annotated class should be cached across multiple ObjectStoreSessions. The ObjectStore
 * will cache these instances and they will be seamlessly available across sessions. If desired, this annotation can
 * specify a cache timeout time in milliseconds measured from caching time.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Cached
{
	/**
	 * @return milliseconds to cache the instances
	 */
	long value() default Long.MAX_VALUE;
}
