package com.taitl.existential.events;

/**
 * Indicates that entity was deleted in the course of of current transaction.
 * 
 * Example: Delete<Account> is raised when Account entity was deleted in the course of current transaction.
 * 
 * Database analog: DELETE
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
public class Delete<T> extends Event<T>
{
    public Delete(T t)
    {
        super(t);
    }
}
