package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to indicate that related member(s) of an object should be automatically parented by the object whose field
 * is annotated with this. This annotation only affects parenting, it does not imply ownership, cascading deletes or
 * anything other than that if this relationship is being traversed in a store operation and the related object needs to
 * be stored, it's Key will be parented.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Child
{
}
