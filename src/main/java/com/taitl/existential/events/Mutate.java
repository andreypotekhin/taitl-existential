package com.taitl.existential.events;

import static com.taitl.existential.constants.Strings.ARG_T0;
import static com.taitl.existential.constants.Strings.ARG_T1;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.events.types.*;

/**
 * Signals a change to an application entity during the current transaction.
 *
 * Unlike {@link Event} classes, provides both the initial and final entity states.
 * Unlike {@link Transit}, both states are required and non-null.
 *
 * Initial state (t0): entity state at the beginning of the transaction.
 * Final state (t1): entity state at the end of the transaction.
 *
 * Example: Mutate<Account> is raised when an Account entity is updated during the current transaction.
 *
 * Database analog: UPDATE
 *
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
        PairArgs.requireBothNonNull(t0, t1, ARG_T0, ARG_T1);
    }
}
