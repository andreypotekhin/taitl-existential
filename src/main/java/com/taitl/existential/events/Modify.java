package com.taitl.existential.events;

import com.taitl.existential.events.types.*;

/**
 * Signals that an entity was created, updated, or deleted during the current transaction.
 *
 * Example: Modify<Account> is raised when an Account entity is created, updated, or deleted during the
 * current transaction.
 *
 * Database analogs: INSERT, UPDATE, DELETE
 *
 * @param <T>
 *            Class of application entity
 */
public class Modify<T> extends EntityEvent<T>
{
    public Modify(T entity)
    {
        super(entity);
    }
}
