package com.taitl.existential.events;

import static com.taitl.existential.constants.Strings.ARG_T0;
import static com.taitl.existential.constants.Strings.ARG_T1;

import com.taitl.existential.events.types.BiEvent;

/**
 * Indicates a change that has happened to an application entity during current transaction.
 * 
 * Unlike Event<T> classes, provides both initial and final states of entity in the course of transaction. Unlike
 * Permutate<T>, initial and final states guaranteed to not be null.
 * 
 * Initial state (t0): entity state in the beginning of transaction Final state (t1): entity state in the end of
 * transaction
 * 
 * Example: Mutate<Account> is raised when Account entity was updated in the course of current transaction.
 * 
 * Database analog: UPDATE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Transit
 */
public class Mutate<T> extends BiEvent<T>
{
    public Mutate(T t0, T t1)
    {
        super(t0, t1);
        if (t0 == null)
        {
            throw new IllegalArgumentException(ARG_T0);
        }
        if (t1 == null)
        {
            throw new IllegalArgumentException(ARG_T1);
        }
    }
}
