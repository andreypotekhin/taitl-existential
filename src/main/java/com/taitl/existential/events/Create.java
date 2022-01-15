package com.taitl.existential.events;

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
 * @see Create
 * @see Update
 * @see Upsert
 * @see Delete
 * @see Read
 * @see Change
 */
public class Create<T> extends Event<T>
{
    public Create(T t)
    {
        super(t);
    }
}
