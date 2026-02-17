package com.taitl.existential.events.transaction_events;

import com.taitl.existential.configs.*;
import com.taitl.existential.events.types.*;

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
