package com.taitl.existential.events;

/**
 * Indicates that entity was created, updated or deleted in the course of of current transaction.
 * 
 * Example: Change<Account> is raised when Account entity was created, updated or deleted in the course of current
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
 * @see Delete
 * @see Read
 * @see Change
 */
public class Change<T> extends Event<T>
{
    public Change(T t)
    {
        super(t);
    }
}
