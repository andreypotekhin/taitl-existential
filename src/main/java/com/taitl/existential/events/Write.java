package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created, updated or deleted in the course of of current transaction.
 *
 * Example: Write<Account> is raised when Account entity was created, updated or deleted in the course of current
 * transaction.
 *
 * Database analogs: INSERT or UPDATE
 *
 * @param <T>
 *            Class of application entity
 */
public class Write<T> extends EntityEvent<T>
{
    public Write(T t)
    {
        super(t);
    }
}
