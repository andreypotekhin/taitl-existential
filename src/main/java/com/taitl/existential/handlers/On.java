package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.handlers.types.EventHandler;
import com.taitl.existential.helper.Args;
import com.taitl.exlogic.handlers.execution.*;

public class On<T> implements EventHandler<T>
{
    public Predicate<? super T> condition;
    public Consumer<? super T> action;
    public String description = null;

    public On(Consumer<? super T> action)
    {
        Args.cool(action, "action");
        this.action = action;
    }

    public On(Consumer<? super T> action, String description)
    {
        Args.cool(action, "action", description, "description");
        this.action = action;
        this.description = description;
    }

    public On(Predicate<? super T> condition, Consumer<? super T> action)
    {
        Args.cool(condition, "condition", action, "action");
        this.condition = condition;
        this.action = action;
    }

    public On(Predicate<? super T> condition, Consumer<? super T> action, String description)
    {
        Args.cool(condition, "condition", description, "description");
        this.condition = condition;
        this.action = action;
        this.description = description;
    }

    public void handle(T t) throws ExistentialException
    {
        // TODO: invoke and handle execute logic in a separate flow,
        // avoiding exposing this class to implementation logic
        // This class is to remain lightweight 'recording purpose' class
        ExecuteEventHandler.handle(this, t);
    }

    /**
     * Has side effects?
     */
    public boolean immutable()
    {
        return action == null;
    }

    public String description()
    {
        return description == null ? "" : description;
    }
}
