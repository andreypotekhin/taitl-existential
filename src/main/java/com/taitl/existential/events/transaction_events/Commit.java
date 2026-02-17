package com.taitl.existential.events.transaction_events;

import com.taitl.existential.configs.*;
import com.taitl.existential.events.types.*;

/**
 * Indicates that transaction was committed.
 *
 * @param <T> Transaction class
 */
public class Commit<T extends Transaction> extends TransactionEvent<T>
{
    public Commit(T t)
    {
        super(t);
    }
}
