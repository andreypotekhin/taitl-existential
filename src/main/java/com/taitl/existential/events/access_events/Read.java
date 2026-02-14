package com.taitl.existential.events.access_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was read (loaded) in the course of current transaction.
 * 
 * Example: Read<Account> is raised when Account entity was loaded in the course
 * of current transaction.
 * 
 * Database analog: SELECT
 * 
 * @param <T>
 *            Class of application entity
 */
public class Read<T> extends EntityEvent<T>
{
    public Read(T t)
    {
        super(t);
    }
}
