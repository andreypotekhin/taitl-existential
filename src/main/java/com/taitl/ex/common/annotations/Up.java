package com.taitl.ex.common.annotations;

import java.lang.annotation.*;

/**
 * Marker annotation a field referencing 'super' or 'parent' component.
 *
 * Usage:
 * @Up
 * protected MyParent parent;
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Up
{
}
