package com.taitl.ex.common.annotations;

import java.lang.annotation.*;

/**
 * Marker annotation fields referencing 'logic' or 'delegate' components.
 *
 * Usage:
 * @Logic
 * protected MyParent parent;
 */
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.FIELD)
public @interface Logic
{
}
