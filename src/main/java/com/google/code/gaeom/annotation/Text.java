package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to specify that a String field is anticipated to contain long text values and therefore should be
 * persisted using the datastore's Text representation. This has the side effect of making this field not indexed.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Text
{
}
