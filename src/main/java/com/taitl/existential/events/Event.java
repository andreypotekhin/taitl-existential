package com.taitl.existential.events;

import com.taitl.existential.constants.Strings;

/**
 * Indicates something that has happened to an application entity. Serves as base class to more specific events
 * (Create<T>, Update<T>, etc.)
 * 
 * Example: Event<Account> is raised when Account entity was accessed or changed in the course of current transaction.
 * 
 * Database analogs: SELECT, INSERT, UPDATE or DELETE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Write
 * @see Delete
 * @see Update
 * @see Upsert
 * @see Read
 * @see ReadAndLock
 * @see Mutate
 * @see Permutate
 */
public class Event<T>
{
    public T t;

    public Event(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        this.t = t;
    }
}
