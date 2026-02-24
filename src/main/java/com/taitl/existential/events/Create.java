package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Signals that an entity was created during the current transaction.
 *
 * Example: Create<Account> is raised when an Account entity is created during the current transaction.
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
