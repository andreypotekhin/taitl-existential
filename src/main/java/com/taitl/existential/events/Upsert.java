package com.taitl.existential.events;

/**
 * Indicates that entity was created or updated in the course of of current transaction.
 * 
 * Example: Upsert<Account> is raised when Account entity was created or updated in the course of current transaction.
 * 
 * Database analogs: INSERT, UPDATE
 * 
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Permutate
 */
public class Upsert<T> extends Event<T>
{
    public Upsert(T t)
    {
        super(t);
    }
}
