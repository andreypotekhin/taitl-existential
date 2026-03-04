package com.taitl.existential.events.types;

import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.combined_events.*;

/**
 * Indicates a change event on an entity, capturing both initial and final states.
 * Serves as a base type for mutation events (Transit<T>, Port<T>).
 * Unlike Event<T>, this event provides the initial and final states of its entity.
 * The initial state (t0) is the entity state at the beginning of the transaction.
 * The final state (t1) is the entity state at the end of the transaction.
 * Example: BiEvent<Account> is emitted when an Account entity changes during a transaction.
 *
 * @param <T>
 *            Class of application entity
 * @see Create
 * @see Delete
 * @see Update
 * @see CU
 * @see Read
 * @see ReadAndLock
 * @see Write
 * @see Transit
 * @see Port
 */
public class BiEvent<T> implements Event<T>
{
    public T t0;
    public T t1;

    /**
     * Creates a change event with initial and final entity state.
     *
     * @param t0 Entity state at the beginning of transaction
     * @param t1 Entity state at the end of transaction
     */
    public BiEvent(T t0, T t1)
    {
        this.t0 = t0;
        this.t1 = t1;
    }

    public int hashCode()
    {
        int result = getClass().hashCode();
        result = 31 * result + System.identityHashCode(t0);
        result = 31 * result + System.identityHashCode(t1);
        return result;
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (other.getClass() != getClass())
        {
            return false;
        }
        BiEvent<?> o = (BiEvent<?>) other;
        return o.t0 == t0 && o.t1 == t1;
    }
}
