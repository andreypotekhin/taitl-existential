package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was updated in the course of of current transaction.
 * 
 * Example: Update<Account> is raised when Account entity was updated in the course of current transaction.
 * 
 * Database analog: UPDATE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Upsert
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class Update<T> extends EntityEvent<T>
{
    public Update(T t)
    {
        super(t);
    }
}
