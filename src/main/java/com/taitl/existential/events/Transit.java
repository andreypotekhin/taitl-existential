package com.taitl.existential.events;

import com.taitl.existential.events.types.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Signals a change to an application entity during the current transaction.
 *
 * Unlike {@link Event} classes, provides both the initial and final entity states.
 * Unlike {@link Port}, both states are required to be non-null.
 *
 * Initial state (before): entity state at the beginning of the transaction.
 * Final state (after): entity state at the end of the transaction.
 *
 * Example: Transit<Account> is raised when an Account entity is updated during the current transaction.
 *
 * Database analog: UPDATE
 *
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Port
 */
public class Transit<T> extends BiEvent<T>
{
    public Transit(T before, T after)
    {
        super(before, after);
        requireBothNonNull(before, after, "Argument 'before' should not be null",
                "Argument 'after' should not be null");
    }
}
