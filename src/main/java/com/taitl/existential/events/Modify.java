package com.taitl.existential.events;

import com.taitl.existential.event.types.EntityEvent;

/**
 * Indicates that entity was created, updated or about to be deleted in the course of current transaction.
 * 
 * Example: Deal<Account> is raised when Account entity was created, updated or about to be deleted 
 * in the course of current transaction.
 * 
 * Database analogs: INSERT, UPDATE or DELETE
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
public class Modify<T> extends EntityEvent<T>
{
    public Modify(T t)
    {
        super(t);
    }
}
