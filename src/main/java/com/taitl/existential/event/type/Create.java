package com.taitl.existential.event.type;

import com.taitl.existential.event.base.EntityEvent;

/**
 * Indicates that entity was created in the course of of current transaction.
 * 
 * Example: Create<Account> is raised when Account entity was created in the course of current transaction.
 * 
 * Database analog: INSERT
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Change
 * @see Update
 * @see Upsert
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class Create<T> extends EntityEvent<T>
{
    public Create(T t)
    {
        super(t);
    }
}
