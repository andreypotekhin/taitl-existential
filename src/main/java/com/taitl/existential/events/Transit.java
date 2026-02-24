package com.taitl.existential.events;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.events.types.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * Signals that an entity was created, updated, or deleted during the current transaction.
 *
 * Unlike {@link Event} classes, provides both the initial and final entity states.
 * Unlike {@link Mutate}, either state may be null.
 *
 * Initial state (t0): entity state at the beginning of the transaction. Null means the entity was created.
 * Final state (t1): entity state at the end of the transaction. Null means the entity was deleted.
 *
 * Example: Transit<Account> is raised when an Account entity is created, updated, or deleted during the
 * current transaction.
 *
 * Database analogs: INSERT, UPDATE, DELETE
 *
 * @param <T>
 *            Class of application entity
 * @see Event
 * @see Mutate
 */
public class Transit<T> extends BiEvent<T>
{
    public Transit(T t0, T t1)
    {
        super(t0, t1);
        PairArgs.requireNotBothNull(t0, t1, ARG_T0_T1);
    }
}
