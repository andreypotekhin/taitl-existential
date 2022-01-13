package com.taitl.existential;

import java.util.function.Predicate;

import com.taitl.existential.exceptions.FailureException;
import com.taitl.existential.exceptions.PredicateFailureException;
import com.taitl.existential.interfaces.Expression;

public class All<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;

    public All(Predicate<? super T> predicate)
    {
        if (predicate == null)
        {
            throw new IllegalArgumentException("Argument 'predicate' should not be null");
        }
        this.predicate = predicate;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        if (condition == null)
        {
            throw new IllegalArgumentException("Argument 'condition' should not be null");
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException("Argument 'predicate' should not be null");
        }
        this.condition = condition;
        this.predicate = predicate;
    }

    public Object evaluate(T t) throws FailureException
    {
        if (condition == null || condition.test(t))
        {
            if (!predicate.test(t))
            {
                throw new PredicateFailureException();
            }
        }
        return null;
    }
}
