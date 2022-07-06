package com.taitl.existential.handlers;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.helper.Args;
import com.taitl.existential.helper.State;

import static com.taitl.existential.constants.Strings.*;

public class On<T> implements EventHandler<T>
{
    Predicate<? super T> condition;
    Consumer<? super T> action;
    String description = null;

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
        if (action == null)
        {
            State.cool(condition, "condition");

            // This is an event handler without side effects.
            // Check the condition and throw an exception if it is not met.
            if (!condition.test(t))
            {
                throw new EventHandlerFailureException(description != null ? description() : CONDITION_NOT_MET);
            }

            return;
        }

        if (condition == null || condition.test(t))
        {
            // if (action instanceof Predicate) {
            // // This is an event handler without side effects.
            // // Check the condition and throw an exception if it is not met.
            // if (!condition.test(t)) {
            // throw new EventHandlerFailureException(description != null ? description() :
            // CONDITION_NOT_MET);
            // }
            // } else {
            try
            {
                action.accept(t);
            }
            catch (Exception e)
            {
                throw new EventHandlerFailureException(e);
            }
            // }
        }
    }

    public String description()
    {
        return description == null ? "" : description;
    }
}
