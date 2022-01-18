package com.taitl.existential.events;

/**
 * Indicates that entity was read (selected, loaded) in the course of of current transaction.
 * 
 * Example: Read<Account> is raised when Account entity was loaded in the course of current transaction.
 * 
 * Database analog: SELECT
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Upsert
 * @see Event
 * @see Delete
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Permutate
 */
public class Read<T> extends Event<T>
{
    public Read(T t)
    {
        super(t);
    }
}

