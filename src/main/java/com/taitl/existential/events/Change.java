package com.taitl.existential.events;

import com.taitl.existential.events.base.EntityEvent;

/**
 * Indicates that entity was updated or about to be deleted in the course of current transaction.
 * 
 * Example: Write<Account> is raised when Account entity was updated or about to be deleted 
 * in the course of current transaction.
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
 * @see Write
 * @see Mutate
 * @see Permutate
 */
public class Change<T> extends EntityEvent<T>
{
    public Change(T t)
    {
        super(t);
    }
}
