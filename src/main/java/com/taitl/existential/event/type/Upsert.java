package com.taitl.existential.event.type;

import com.taitl.existential.event.base.EntityEvent;

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
public class Upsert<T> extends EntityEvent<T>
{
    public Upsert(T t)
    {
        super(t);
    }
}
