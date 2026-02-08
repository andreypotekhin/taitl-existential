package com.taitl.existential.events.transaction_events;

import com.taitl.existential.events.types.*;
import com.taitl.existential.transactions.*;

/**
 * Indicates that transaction was started.
 *
 * @param <T> Transaction class
 */
public class Begin<T extends Transaction> extends TransactionEvent<T>
{
    public Begin(T t)
    {
        super(t);
    }
}
