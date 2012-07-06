package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that a type or a field should be embedded inside a host object as opposed to having an entity
 * type of its own. With datastore, this is a great way to improve performance as you can pull one entity and have
 * embedded instances of related objects.
 * 
 * NOTE: you can embed arbitrarily deep explicit hierarchies of objects - but you cannot embed multiple to-many
 * (collection) relationships into a host entity.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
		ElementType.FIELD, ElementType.TYPE
})
public @interface Embedded
{
}
