package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created or updated in the course of of current transaction.
 *
 * Example: Upsert<Account> is raised when Account entity was created or updated in the course of current transaction.
 *
 * Database analogs: INSERT or UPDATE
 *
 * @param <T>
 *            Class of application entity
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
