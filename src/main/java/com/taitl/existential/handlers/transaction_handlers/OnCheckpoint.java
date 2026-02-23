package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.handlers.*;

/**
 * Handles transaction checkpoint events for a transaction type.
 *
 * @param <T>
 *            Transaction type
 */
public class OnCheckpoint<T extends Transaction> extends On<T>
{
    /**
     * Creates a handler that always runs the provided action.
     *
     * @param action
     *            Side effect to execute on checkpoint
     */
    public OnCheckpoint(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a handler with a description.
     *
     * @param action
     *            Side effect to execute on checkpoint
     * @param description
     *            Human-readable description of the handler
     */
    public OnCheckpoint(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a handler that runs only when the predicate holds.
     *
     * @param condition
     *            Predicate that must be satisfied
     * @param action
     *            Side effect to execute on checkpoint
     */
    public OnCheckpoint(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a handler with a predicate and description.
     *
     * @param condition
     *            Predicate that must be satisfied
     * @param action
     *            Side effect to execute on checkpoint
     * @param description
     *            Human-readable description of the handler
     */
    public OnCheckpoint(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }
}
