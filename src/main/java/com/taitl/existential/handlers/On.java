package com.taitl.existential.handlers;

import static com.taitl.existential.constants.Strings.ARG_CONDITION;
import static com.taitl.existential.constants.Strings.ARG_PREDICATE;

import java.util.function.Consumer;
import java.util.function.Predicate;

import com.taitl.existential.exceptions.EventHandlerFailureException;
import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.handler.types.EventHandler;

public class On<T> implements EventHandler<T>
{
    Predicate<? super T> condition;
    Consumer<? super T> action;

    public On(Consumer<? super T> action)
    {
        if (action == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.action = action;
    }

    public On(Predicate<? super T> condition, Consumer<? super T> action)
    {
        if (condition == null)
        {
            throw new IllegalArgumentException(ARG_CONDITION);
        }
        if (action == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.condition = condition;
        this.action = action;
    }

    public void handle(T t) throws ExistentialException
    {
        if (condition == null || condition.test(t))
        {
            try
            {
                action.accept(t);
            }
            catch (Exception e)
            {
                throw new EventHandlerFailureException(e);
            }
        }
    }
}