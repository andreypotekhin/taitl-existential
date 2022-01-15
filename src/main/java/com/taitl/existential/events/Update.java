package com.taitl.existential.events;

/**
 * Indicates that entity was updated in the course of of current transaction.
 * 
 * Example: Update<Account> is raised when Account entity was updated in the course of current transaction.
 * 
 * Database analog: UPDATE
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
public class Update<T> extends Event<T>
{
    public Update(T t)
    {
        super(t);
    }
}
