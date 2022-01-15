package com.taitl.existential.events;

/**
 * Indicates something that has happened to an application entity. Serves as base class to mutation events (Mutate<T>,
 * Permutate<T>)
 * 
 * Unlike Event<T> class, provides initial and final entity states for the transaction.
 * 
 * Initial state (t0): entity state in the beginning of transaction Final state (t1): entity state in the end of
 * transaction
 * 
 * Example: BiEvent<Account> is raised when Account entity was changed in the course of current transaction.
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Permutate
 */
public class BiEvent<T>
{
    public T t0;
    public T t1;

    public BiEvent(T t0, T t1)
    {
        this.t0 = t0;
        this.t1 = t1;
    }
}
