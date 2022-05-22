package com.taitl.existential.events;

import com.taitl.existential.event.types.EntityEvent;

/**
 * Indicates that entity was created or updated in the course of of current transaction.
 *
 * Example: Upsert<Account> is raised when Account entity was created or updated in the course of current transaction.
 *
 * Database analogs: UPDATE or DELETE
 *
 *
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Create
 * @see Update
 * @see Upsert
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
// TODO: is this event needed? Should we just use Update instead of Update, Upsert, Modify and
// Change?
public class Updel<T> extends EntityEvent<T>
{
    public Updel(T t)
    {
        super(t);
    }
}
