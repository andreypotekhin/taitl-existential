package com.taitl.ex.logic.execution.data;

import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;

/**
 * A compact cache key describing which handlers a reported event can select.
 * It includes the declared type text, descriptor and runtime class identities, event class,
 * name/splitting modes, transaction/memo availability, and presence of explicit bi-event endpoints.
 * The owning EventField supplies the operation, stage, and rule-inventory generation.
 *
 * Constructing the key precedes generic hierarchy and event-family expansion. It retains no
 * event instance, entity, transaction, or memo value, allowing selections to be reused safely
 * across entities and transactions with the same dispatch shape.
 */
public class DispatchSignature
{
    protected String type;
    protected Class<?> descriptorClass;
    protected Class<?> runtimeClass;
    protected Class<?> eventClass;
    protected int shape;
    protected int hash;

    public DispatchSignature(RuntimeKey<?> key, boolean fullNames, boolean split, boolean transaction, boolean memo)
    {
        type = key.typeKey().toString();
        descriptorClass = key.typeKey().getClass();
        runtimeClass = key.entity() == null ? null : key.entity().getClass();
        eventClass = key.event().getClass();
        shape = (fullNames ? 1 : 0) | (split ? 2 : 0) | (transaction ? 4 : 0) | (memo ? 8 : 0);
        if (key.event() instanceof BiEvent<?>)
        {
            BiEvent<?> event = (BiEvent<?>) key.event();
            shape |= (event.t0 != null ? 16 : 0) | (event.t1 != null ? 32 : 0);
        }
        hash = computeHash();
    }

    protected int computeHash()
    {
        int result = type.hashCode();
        result = 31 * result + descriptorClass.hashCode();
        result = 31 * result + (runtimeClass == null ? 0 : runtimeClass.hashCode());
        result = 31 * result + eventClass.hashCode();
        return 31 * result + shape;
    }

    public int hashCode()
    {
        return hash;
    }

    public boolean equals(Object other)
    {
        if (!(other instanceof DispatchSignature))
        {
            return false;
        }
        DispatchSignature key = (DispatchSignature) other;
        return shape == key.shape && descriptorClass == key.descriptorClass && runtimeClass == key.runtimeClass
                && eventClass == key.eventClass && type.equals(key.type);
    }
}
