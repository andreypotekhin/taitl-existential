package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created or updated in the course of of current transaction.
 *
 * Example: Upsert<Account> is raised when Account entity was created or updated in the course of current transaction.
 *
 * Database analogs: INSERT or UPDATE
 *
 *
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
// TODO: is this event needed? Should we just use Update instead of Update, Upsert, Modify and
// Change?
public class Upsert<T> extends EntityEvent<T>
{
    public Upsert(T t)
    {
        super(t);
    }
}
