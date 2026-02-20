package com.taitl.existential.events.combined_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was updated or deleted in the course of current transaction.
 *
 * Example: UD<Account> is raised when Account entity was updated or deleted in the course of current
 * transaction.
 *
 * Database analogs: UPDATE or DELETE
 *
 * @param <T> Class of application entity
 */
public class UD<T> extends EntityEvent<T>
{
    public UD(T t)
    {
        super(t);
    }
}
