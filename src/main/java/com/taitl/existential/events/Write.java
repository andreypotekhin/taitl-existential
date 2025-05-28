package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created, updated or deleted in the course of of current transaction.
 * 
 * Example: Write<Account> is raised when Account entity was created, updated or deleted in the course of current
 * transaction.
 * 
 * Database analogs: INSERT or UPDATE
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
 * @see Mutate
 * @see Transit
 */
public class Write<T> extends EntityEvent<T>
{
    public Write(T t)
    {
        super(t);
    }
}
