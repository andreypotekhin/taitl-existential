package com.taitl.existential.handlers.transaction_handlers;

import java.util.function.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.events.transaction_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.*;

/**
 * Declares a handler that runs when a transaction is committed.
 *
 * @param <T>
 *            Transaction type
 */
public class OnCommit<T extends Transaction> extends On<T>
{
    /**
     * Creates a commit handler that always executes.
     *
     * @param action
     *            Action to invoke when the transaction commits
     */
    public OnCommit(Consumer<? super T> action)
    {
        super(action);
    }

    /**
     * Creates a commit handler with a description.
     *
     * @param action
     *            Action to invoke when the transaction commits
     * @param description
     *            Human-friendly description of the handler
     */
    public OnCommit(Consumer<? super T> action, String description)
    {
        super(action, description);
    }

    /**
     * Creates a conditional commit handler.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the transaction commits
     */
    public OnCommit(Predicate<? super T> condition, Consumer<? super T> action)
    {
        super(condition, action);
    }

    /**
     * Creates a conditional commit handler with a description.
     *
     * @param condition
     *            Predicate that decides whether the handler runs
     * @param action
     *            Action to invoke when the transaction commits
     * @param description
     *            Human-friendly description of the handler
     */
    public OnCommit(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        super(condition, action, description);
    }

    public EventType eventType()
    {
        return EventType.valueOf(Commit.class);
    }
}
