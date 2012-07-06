package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to specify that a field represents a relationship to an object that should be used to parent the key
 * for the object that has the annotated field. By default, when this annotation is used, the field is still persisted
 * as a relationship so that it can be used for querying etc. If desired, the annotation can specify that the framework
 * should not store the key, but either not allow filtering on this field or seamlessly change to an ancestor query if a
 * filter is specified on this field. The latter requires that the object-graph support an ancestor query to find the
 * parent because, of course, it is an ancestor query, not a parent query.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Parent
{
	public static enum FilterPolicy
	{
		NoFilter, RetainKey, AncestorQuery
	}

	public FilterPolicy value() default FilterPolicy.RetainKey;
}
