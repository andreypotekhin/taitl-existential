package com.taitl.existential.events;

import com.taitl.existential.events.base.EntityEvent;

/**
 * Indicates that entity was read with update lock (selected for update) in the course of of current transaction.
 * 
 * Example: ReadAndLock<Account> is raised when Account entity was loaded in the course of current transaction.
 * 
 * Database analog: SELECT FOR UPDATE
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
public class ReadAndLock<T> extends EntityEvent<T>
{
    public ReadAndLock(T t)
    {
        super(t);
    }
}
