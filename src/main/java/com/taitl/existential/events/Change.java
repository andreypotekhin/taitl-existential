package com.taitl.existential.events;

import com.taitl.existential.event.types.EntityEvent;

/**
 * Indicates that entity was updated in the course of current transaction.
 * 
 * Example: Change<Account> is raised when Account entity was updated
 * in the course of current transaction.
 * 
 * Database analog: UPDATE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Upsert
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class Change<T> extends EntityEvent<T>
{
    public Change(T t)
    {
        super(t);
    }
}
