package com.taitl.existential.events.access_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was read with an update lock (selected for update) in the course of current transaction.
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
