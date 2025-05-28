package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was deleted in the course of of current transaction.
 * 
 * Example: Delete<Account> is raised when Account entity was deleted in the course of current transaction.
 * 
 * Database analog: DELETE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Upsert
 * @see Event
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class Delete<T> extends EntityEvent<T>
{
    public Delete(T t)
    {
        super(t);
    }
}
