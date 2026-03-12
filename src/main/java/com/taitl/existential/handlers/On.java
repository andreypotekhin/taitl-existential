package com.taitl.existential.handlers;

import com.taitl.ex.common.helper.strings.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;

import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Event handler for On event (that is, matching any event).
 *
 * Base handler declaration that captures an optional condition, an action,
 * and a human-friendly description for an event.
 *
 * This type is meant to be lightweight and declarative; the actual execution
 * is delegated to the handler runtime.
 *
 * @param <T>
 *            Type of entity or value handled by the event
 *
 * @see On
 */
public class On<T> implements EventHandler<T>
{
    public Predicate<? super T> condition;
    public Consumer<? super T> action;
    public String description = null;

    /**
     * Creates a handler with an action that always executes, annotated
     * with a human-readable description.
     *
     * @param action
     *            Action to invoke when the event is handled
     * @param description
     *            Human-friendly description of the handler
     */
    public On(Consumer<? super T> action, String description)
    {
        this(null, action);
        this.description = description;
    }

    /**
     * Creates a handler declaration with optional condition and action.
     *
     * At least one of condition or action must be provided:
     * - condition + no action: immutable invariant check
     * - no condition + action: always-running side effect
     * - condition + action: conditional side effect
     *
     * @param condition
     *            Predicate that determines whether the action should run
     * @param action
     *            Action to invoke when the event is handled
     */
    public On(Predicate<? super T> condition, Consumer<? super T> action)
    {
        check(condition != null || action != null,
                "Either 'condition' or 'action' must not be null");
        this.condition = condition;
        this.action = action;
    }

    /**
     * Creates a handler declaration with an explicit description.
     *
     * @param condition
     *            Optional predicate that determines whether the action should run
     * @param action
     *            Optional action to invoke when the event is handled
     * @param description
     *            Human-friendly description of the handler
     */
    public On(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        this(condition, action);
        this.description = description;
    }

    /**
     * Handles the event using the runtime execution pipeline.
     *
     * @param t
     *            Entity or value associated with the event
     * @throws ExistentialException
     *            If handler execution fails
     */
    public void handle(T t) throws ExistentialException
    {
        ExecuteHandler.handle(this, t);
    }

    /**
     * Reports whether this handler is immutable (no action attached).
     *
     * @return true when the handler has no action and therefore no side effects
     */
    public boolean immutable()
    {
        return action == null;
    }

    /**
     * Returns the handler description or an empty string if absent.
     *
     * @return human-friendly description for diagnostics/logging
     */
    public String description()
    {
        return Descriptions.text(description);
    }
}
