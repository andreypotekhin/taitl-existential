package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import java.lang.reflect.*;

/**
 * Represents a single native upper bound when rendering legacy expanded descriptors.
 * It exposes that bound through WildcardType and renders either short or full class names,
 * falling back to the binary name when a canonical name is unavailable.
 *
 * The value has no lower bound and retains only class metadata. It supports legacy key
 * construction; structural selection checks native argument relationships directly.
 */
class ResolvedWildcardType implements WildcardType
{
    private final Class<?> bound;
    private final boolean useFullName;

    ResolvedWildcardType(Class<?> bound, boolean useFullName)
    {
        this.bound = bound;
        this.useFullName = useFullName;
    }

    public Type[] getUpperBounds()
    {
        return new Type[] { bound };
    }

    public Type[] getLowerBounds()
    {
        return new Type[0];
    }

    public String getTypeName()
    {
        String name = useFullName ? bound.getCanonicalName() : bound.getSimpleName();
        return "? extends " + (name != null ? name : bound.getName());
    }

    public String toString()
    {
        return getTypeName();
    }
}
