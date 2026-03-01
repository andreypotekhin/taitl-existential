package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Signals that an entity was deleted during the current transaction.
 * 
 * Example: Delete<Account> is raised when an Account entity was deleted during the current transaction.
 * 
 * Database analog: DELETE
 * 
 * @param <T>
 *            Class of application entity
 */
public class Delete<T> extends EntityEvent<T>
{
    public Delete(T entity)
    {
        super(entity);
    }
}
