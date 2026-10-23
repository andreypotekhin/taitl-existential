package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import java.lang.reflect.*;

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
