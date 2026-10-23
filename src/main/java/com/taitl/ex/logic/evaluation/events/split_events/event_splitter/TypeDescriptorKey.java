package com.taitl.ex.logic.evaluation.events.split_events.event_splitter;

import com.taitl.existential.keys.*;

import java.lang.ref.*;

/**
 * Identifies an expanded descriptor by text, class identities, and naming mode.
 * Weak references distinguish runtime and TypeKey subclasses without retaining their application
 * class loaders in the shared descriptor cache. The precomputed hash remains stable after
 * collection, allowing stale keys to leave through normal bounded eviction.
 *
 * A collected class prevents equality with a new key. Live referents are captured during
 * comparison so collection cannot change the identity comparison partway through a lookup.
 */
final class TypeDescriptorKey
{
    private final WeakReference<Class<?>> descriptorClass;
    private final String descriptor;
    private final WeakReference<Class<?>> runtimeClass;
    private final boolean useFullName;
    private final int hash;

    TypeDescriptorKey(TypeKey<?> typeKey, Class<?> runtimeClass, boolean useFullName)
    {
        this.descriptorClass = new WeakReference<>(typeKey.getClass());
        this.descriptor = typeKey.toString();
        this.runtimeClass = new WeakReference<>(runtimeClass);
        this.useFullName = useFullName;
        int value = 31 * typeKey.getClass().hashCode() + descriptor.hashCode();
        this.hash = 31 * (31 * value + runtimeClass.hashCode()) + (useFullName ? 1 : 0);
    }

    public int hashCode()
    {
        return hash;
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
        Class<?> descriptorType = descriptorClass.get();
        Class<?> runtimeType = runtimeClass.get();
        return descriptorType != null && runtimeType != null
                && descriptorType == key.descriptorClass.get() && descriptor.equals(key.descriptor)
                && runtimeType == key.runtimeClass.get() && useFullName == key.useFullName;
    }
}
