package com.taitl.existential.events.types;

import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;

/**
 * Indicates an event on an entity, such as creating, accessing, modifying, or deleting it.
 * Serves as a base type for more specific events, such as {@code Create<T>} and {@code Update<T>}.
 * Example: {@code EntityEvent<Account>} is emitted when an {@code Account} entity is accessed
 * or modified during a business transaction.
 * Database analogs include SELECT, INSERT, UPDATE, and DELETE.
 *
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see Upsert
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class EntityEvent<T> implements Event<T>
{
    public T t;

    public EntityEvent(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        this.t = t;
    }
}
