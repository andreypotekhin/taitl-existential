package com.taitl.existential.events.combined_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was created, updated or deleted in the course of current transaction.
 *
 * Example: CUD<Account> is raised when Account entity was created, updated or deleted in the course of current
 * transaction.
 *
 * Database analogs: INSERT or UPDATE or DELETE
 *
 * @param <T> Class of application entity
 */
public class CUD<T> extends EntityEvent<T>
{
    public CUD(T t)
    {
        super(t);
    }
}
