package com.taitl.existential.events.transaction_events;

import com.taitl.existential.configs.*;
import com.taitl.existential.events.types.*;

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
