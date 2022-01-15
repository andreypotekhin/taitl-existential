package com.taitl.existential.events;

import static com.taitl.existential.constants.Strings.ARG_T0_T1;

/**
 * Indicates that an application entity has been created, changed or deleted during current transaction.
 * 
 * Unlike Event<T> classes, provides both initial and final states of entity in the course of transaction. Unlike
 * Mutate<T>, one of initial or final states is allowed to be null. Null in initial state indicates the entity has been
 * created. Null in final state indicates the entity has been deleted.
 * 
 * Initial state (t0): entity state in the beginning of transaction. Null indicates the entity has been created. Final
 * state (t1): entity state in the end of transaction. Null indicates the entity has been deleted.
 * 
 * Example: Permutate<Account> is raised when Account entity has been created, updated or deleted in the course of
 * current transaction.
 * 
 * Database analogs: INSERT, UPDATE, DELETE
 * 
 * @author Andrey Potekhin
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Mutate
 */
public class Permutate<T> extends BiEvent<T>
{
    public Permutate(T t0, T t1)
    {
        super(t0, t1);
        if (t0 == null && t1 == null)
        {
            throw new IllegalArgumentException(ARG_T0_T1);
        }
    }
}
