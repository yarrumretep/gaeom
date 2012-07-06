package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An anotation to indicate that a field should not be indexed by the datastore. This has the effect of making the
 * values in this field not useful in query filters {@link com.google.code.gaeom.Find#filter(String,
 * com.google.code.gaeom.Find.Op, Object)}
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface NoIndex
{
}
