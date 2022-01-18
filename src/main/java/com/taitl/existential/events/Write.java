package com.taitl.existential.events;

/**
 * Indicates that entity was created, updated or deleted in the course of of current transaction.
 * 
 * Example: Write<Account> is raised when Account entity was created, updated or deleted in the course of current
 * transaction.
 * 
 * Database analogs: INSERT, UPDATE or DELETE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Update
 * @see Upsert
 * @see Event
 * @see Delete
 * @see Read
 * @see ReadAndLock
 * @see Mutate
 * @see Permutate
 */
public class Write<T> extends Event<T>
{
    public Write(T t)
    {
        super(t);
    }
}
