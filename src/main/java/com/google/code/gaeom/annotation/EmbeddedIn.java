package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that the field should be populated with a 'backpointer' to an embedding instance rather than
 * resulting in a key storage field directly. Practically this means the relationship can be stored without using any
 * datastore space. Upon object hydration, the framework will wire up these fields based upon the context within which
 * the objects are hydrating.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
	ElementType.FIELD
})
public @interface EmbeddedIn
{
}
