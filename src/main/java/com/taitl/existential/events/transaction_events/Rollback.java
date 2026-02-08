package com.taitl.existential.events.transaction_events;

import com.taitl.existential.events.types.*;
import com.taitl.existential.transactions.*;

/**
 * Indicates that transaction was rolled back.
 *
 * @param <T> Transaction class
 */
public class Rollback<T extends Transaction> extends TransactionEvent<T>
{
    public Rollback(T t)
    {
        super(t);
    }
}
