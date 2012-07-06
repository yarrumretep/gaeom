package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that a field is to be stored as a serialized blob. The field type must implement
 * Serializable.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Serialize
{
}
