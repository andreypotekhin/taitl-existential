package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;

/**
 * Indicates change event on an entity, recording entities both initial and final states.
 * Serves as base class to the mutation events ({@code Mutate<T>, Transit<T>}).
 * Unlike {@code Event<T>} class, provides the initial and final states of its entity.
 * The initial state (t0) is entity state at the beginning of transaction.
 * The final state (t1) is entity state at the end of transaction.
 * Example: BiEvent<Account> is sent when an Account entity gets changed in the course of transaction.
 *
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see Upsert
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Mutate
 * @see Transit
 */
public class BiEvent<T> implements Event<T>
{
    public T t0;
    public T t1;

    public BiEvent(T t0, T t1)
    {
        this.t0 = t0;
        this.t1 = t1;
    }
}
