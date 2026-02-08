package com.taitl.existential.events.types;

import com.taitl.existential.constants.*;
import com.taitl.existential.transactions.*;

/**
 * Transaction lifecycle event: begin, commit or rollback a transaction.
 * Serves as base class to specific events ({@code Begin<T>, Commit<T>,} etc.)
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
            throw new IllegalArgumentException(Strings.ARG_T);
        }
        this.t = t;
    }
}
