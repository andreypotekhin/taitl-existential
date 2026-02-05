package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was deleted in the course of of current transaction.
 * 
 * Example: Delete<Account> is raised when Account entity was deleted in the course of current transaction.
 * 
 * Database analog: DELETE
 * 
 * @param <T>
 *            Class of application entity
 */
public class Delete<T> extends EntityEvent<T>
{
    public Delete(T t)
    {
        super(t);
    }
}
