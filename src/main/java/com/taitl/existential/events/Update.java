package com.taitl.existential.events;

import com.taitl.existential.events.types.*;

/**
 * Signals that an entity was updated during the current transaction.
 *
 * Example: Update<Account> is raised when an Account entity was updated during the current transaction.
 *
 * Database analog: UPDATE
 *
 * @param <T>
 *            Class of application entity
 */
public class Update<T> extends EntityEvent<T>
{
    public Update(T entity)
    {
        super(entity);
    }
}
