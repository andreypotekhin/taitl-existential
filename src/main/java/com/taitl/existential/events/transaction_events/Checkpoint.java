package com.taitl.existential.events.transaction_events;

import com.taitl.existential.events.types.*;
import com.taitl.existential.transactions.*;

/**
 * Indicates transaction checkpoint.
 *
 * @param <T> Transaction class
 */
public class Checkpoint<T extends Transaction> extends TransactionEvent<T>
{
    public Checkpoint(T t)
    {
        super(t);
    }
}
