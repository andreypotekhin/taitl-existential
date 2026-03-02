package com.taitl.existential.events;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.events.types.*;

/**
 * Signals a change to an application entity during the current transaction.
 *
 * Unlike {@link Event} classes, provides both the initial and final entity states.
 * Unlike {@link Transit}, both states are required and non-null.
 *
 * Initial state (before): entity state at the beginning of the transaction.
 * Final state (after): entity state at the end of the transaction.
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
// TODO: rename to Transit
public class Mutate<T> extends BiEvent<T>
{
    public Mutate(T before, T after)
    {
        super(before, after);
        PairArgs.requireBothNonNull(before, after, "Argument 'before' should not be null",
                "Argument 'after' should not be null");
    }
}
