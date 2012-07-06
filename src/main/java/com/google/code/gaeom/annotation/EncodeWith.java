package com.google.code.gaeom.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.code.gaeom.CustomEncoder;

/**
 * Annotation to indicate that the user has specified a custom encoder for the field or type. The annotation must supply
 * a class implementing either {@link com.google.code.gaeom.CustomEncoder} or
 * {@link com.google.code.gaeom.CustomMultiEncoder} which will then be used to encode / decode values.
 * 
 * @author Peter Murray <gaeom@pmurray.com>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{
		ElementType.FIELD, ElementType.TYPE
})
public @interface EncodeWith
{
	Class<? extends CustomEncoder<?, ?>> value();
}
