package com.taitl.existential.events.combined_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was created or updated in the course of current transaction.
 *
 * Example: CU<Account> is raised when Account entity was created or updated in the course of current transaction.
 *
 * Database analogs: INSERT or UPDATE
 *
 * @param <T>
 *            Class of application entity
 */
public class CU<T> extends EntityEvent<T>
{
    public CU(T t)
    {
        super(t);
    }
}
