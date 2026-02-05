package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was updated in the course of current transaction.
 * 
 * Example: Change<Account> is raised when Account entity was updated
 * in the course of current transaction.
 * 
 * Database analog: UPDATE
 * 
 * @param <T>
 *            Class of application entity
 */
public class Change<T> extends EntityEvent<T>
{
    public Change(T t)
    {
        super(t);
    }
}
