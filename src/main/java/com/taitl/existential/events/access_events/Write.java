package com.taitl.existential.events.access_events;

import com.taitl.existential.events.types.*;

/**
 * Indicates that entity was saved in the course of current transaction.
 *
 * Example: Write<Account> is raised when Account entity was saved in the course
 * of current transaction.
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
