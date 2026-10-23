package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.keys.*;

import java.util.*;

final class TypeDescriptorKey
{
    private final Class<?> descriptorClass;
    private final String descriptor;
    private final Class<?> runtimeClass;
    private final boolean useFullName;

    TypeDescriptorKey(TypeKey<?> typeKey, Class<?> runtimeClass, boolean useFullName)
    {
        this.descriptorClass = typeKey.getClass();
        this.descriptor = typeKey.toString();
        this.runtimeClass = runtimeClass;
        this.useFullName = useFullName;
    }

    public int hashCode()
    {
        return Objects.hash(descriptorClass, descriptor, runtimeClass, useFullName);
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof TypeDescriptorKey))
        {
            return false;
        }
        TypeDescriptorKey key = (TypeDescriptorKey) other;
        return descriptorClass == key.descriptorClass && descriptor.equals(key.descriptor)
                && runtimeClass == key.runtimeClass && useFullName == key.useFullName;
    }
}
