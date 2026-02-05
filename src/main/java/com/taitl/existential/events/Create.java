package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created in the course of current transaction.
 * 
 * Example: Create<Account> is raised when Account entity was created in the course of current transaction.
 * 
 * Database analog: INSERT
 * 
 * @param <T>
 *            Class of application entity
 */
public class Create<T> extends EntityEvent<T>
{
    public Create(T t)
    {
        super(t);
    }
}
