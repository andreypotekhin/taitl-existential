package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.events.transaction_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

/**
 * Declares a handler that runs when a transaction rolls back.
 *
 * @param <T>
 *            Transaction type
 */
public class OnRollback<T extends Transaction> extends On<T>
{
    /**
     * Creates a rollback handler that always executes.
     *
     * @param action
     *            Action to invoke when the transaction rolls back
     */
    public OnRollback(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a rollback handler with a description.
     *
     * @param action
     *            Action to invoke when the transaction rolls back
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRollback(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a conditional rollback handler.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the transaction rolls back
     */
    public OnRollback(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a conditional rollback handler with a description.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the transaction rolls back
     * @param description
     *            Human-friendly description of the handler
     */
    public OnRollback(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Rollback.class);
    }
}
