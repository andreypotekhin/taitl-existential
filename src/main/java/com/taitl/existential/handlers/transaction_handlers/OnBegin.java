package com.taitl.existential.handlers.transaction_handlers;

import com.taitl.existential.configs.*;
import com.taitl.existential.events.transaction_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

import java.util.function.*;

/**
 * Event handler for transaction Begin events.
 *
 * @param <T>
 *            Transaction type handled by the begin event
 */
public class OnBegin<T extends Transaction> extends On<T>
{

    public OnBegin(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    public OnBegin(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    public OnBegin(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Begin.class);
    }
}
