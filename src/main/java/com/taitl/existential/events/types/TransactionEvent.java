package com.taitl.existential.events.types;

import com.taitl.existential.configs.*;
import com.taitl.existential.events.transaction_events.*;

/**
 * Indicates a transaction lifecycle event, such as begin, commit, checkpoint, or rollback.
 * Serves as a base type for specific events such as Begin<T> and Commit<T>.
 *
 * @param <T>
 *            Transaction class
 * @see Begin
 * @see Commit
 * @see Rollback
 */
public class TransactionEvent<T extends Transaction> implements Event<T>
{
    public T t;

    public TransactionEvent(T t)
    {
        if (t == null)
        {
            throw new IllegalArgumentException("Argument 't' should not be null");
        }
        this.t = t;
    }
}
