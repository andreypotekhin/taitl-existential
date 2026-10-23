package com.taitl.ex.logic.evaluation.events.split_events.event_splitter.type_keys;

import java.lang.reflect.*;
import java.util.*;

class ResolvedParameterizedType implements ParameterizedType
{
    private final Class<?> raw;
    private final Type[] arguments;
    private final Type owner;

    ResolvedParameterizedType(Class<?> raw, Type[] arguments, Type owner)
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
                .toArray(String[]::new)) + ">";
    }
}
