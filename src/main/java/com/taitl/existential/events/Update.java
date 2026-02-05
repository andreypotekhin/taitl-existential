package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was updated in the course of of current transaction.
 * 
 * Example: Update<Account> is raised when Account entity was updated in the course of current transaction.
 * 
 * Database analog: UPDATE
 * 
 * @param <T>
 *            Class of application entity
 */
public class Update<T> extends EntityEvent<T>
{
    public Update(T t)
    {
        super(t);
    }
}
