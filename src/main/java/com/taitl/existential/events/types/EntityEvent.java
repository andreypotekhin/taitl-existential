package com.taitl.existential.events.types;

import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;

/**
 * Indicates an event on an entity, such as of creating, accessing, modifying or deleting.
 * Serves as base class to more specific events, such as {@code Create<T>, Update<T>}.
 * Example: {@code EntityEvent<Account>} is emitted when an Account entity has been accessed
 * or modified in the course of business transaction.
 * Database analogs: SELECT, INSERT, UPDATE or DELETE
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
