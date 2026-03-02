package com.taitl.existential.events;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.events.types.*;

/**
 * Signals that an entity was created, updated, or deleted during the current transaction.
 *
 * Unlike {@link Event} classes, provides both the initial and final entity states.
 * Unlike {@link Mutate}, either state may be null.
 *
 * Initial state (before): entity state at the beginning of the transaction. Null means the entity was created.
 * Final state (after): entity state at the end of the transaction. Null means the entity was deleted.
 *
 * Example: Port<Account> is raised when an Account entity is created, updated, or deleted during the
 * current transaction.
 *
 * Database analogs: INSERT, UPDATE, DELETE
 *
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Mutate
 */
public class Port<T> extends BiEvent<T>
{
    public Port(T before, T after)
    {
        super(before, after);
        PairArgs.requireNotBothNull(before, after, "Arguments 'before' and 'after' should not be both null");
    }
}
