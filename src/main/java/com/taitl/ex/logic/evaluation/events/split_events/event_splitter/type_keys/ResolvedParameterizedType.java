package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import java.lang.reflect.*;
import java.util.*;

/**
 * Represents a parameterized ancestor after generic argument substitution.
 * TypeHierarchyLogic creates this descriptor when a child's concrete arguments flow into a
 * superclass or interface signature. Raw type and owner metadata remain available to TypeKey
 * rendering and structural upper-bound matching.
 *
 * Argument arrays are copied both on input and access so published hierarchy metadata cannot
 * be changed by callers. The descriptor carries type metadata only and never an entity value.
 */
public class ResolvedParameterizedType implements ParameterizedType
{
    private final Class<?> raw;
    private final Type[] arguments;
    private final Type owner;

    public ResolvedParameterizedType(Class<?> raw, Type[] arguments, Type owner)
    {
        this.raw = raw;
        this.arguments = arguments.clone();
        this.owner = owner;
    }

    public Type[] getActualTypeArguments()
    {
        return arguments.clone();
    }

    public Type getRawType()
    {
        return raw;
    }

    public Type getOwnerType()
    {
        return owner;
    }

    public String getTypeName()
    {
        return raw.getTypeName() + "<" + String.join(",", Arrays.stream(arguments)
                .map(argument -> argument.getTypeName())
                .toArray(size -> new String[size])) + ">";
    }
}
