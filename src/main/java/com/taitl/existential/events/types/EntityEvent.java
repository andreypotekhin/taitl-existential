package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;

/**
 * Indicates an event that targets a single entity instance, such as creating, accessing, modifying,
 * or deleting it.
 * Serves as a base type for more specific events, such as Create<T> and Update<T>.
 * Example: EntityEvent<Account> is emitted when an Account entity is accessed or modified during a
 * business transaction.
 * Database analogs include SELECT, INSERT, UPDATE, and DELETE.
 *
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see CU
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Transit
 * @see Port
 */
public class EntityEvent<T> implements Event<T>
{
    public T t;

    public EntityEvent(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Argument 't' should not be null");
        }
        this.t = t;
    }
}
