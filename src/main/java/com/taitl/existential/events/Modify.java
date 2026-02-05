package com.taitl.existential.events;

import com.taitl.existential.events.types.EntityEvent;

/**
 * Indicates that entity was created, updated or about to be deleted in the course of current transaction.
 * 
 * Example: Deal<Account> is raised when Account entity was created, updated or about to be deleted 
 * in the course of current transaction.
 * 
 * Database analogs: INSERT, UPDATE or DELETE
 * 
 * @param <T>
 *            Class of application entity
 */
public class Modify<T> extends EntityEvent<T>
{
    public Modify(T t)
    {
        super(t);
    }
}
