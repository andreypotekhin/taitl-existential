package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was read with update lock (selected for update) in the course of of current transaction.
 * 
 * Example: ReadAndLock<Account> is raised when Account entity was loaded in the course of current transaction.
 * 
 * Database analog: SELECT FOR UPDATE
 * 
 * @param <T>
 *            Class of application entity
 */
public class ReadAndLock<T> extends EntityEvent<T>
{
    public ReadAndLock(T t)
    {
        super(t);
    }
}
