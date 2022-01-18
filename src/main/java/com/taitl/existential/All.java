package com.taitl.existential;

import static com.taitl.existential.constants.Strings.ARG_CONDITION;
import static com.taitl.existential.constants.Strings.ARG_PREDICATE;

import java.util.function.Predicate;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.exceptions.PredicateFailureException;
import com.taitl.existential.interfaces.Expression;
import com.taitl.existential.unused.Exists0;

/**
 * Implements "For Any" (universal quantification) notation for reasoning about application entities. See library
 * documentation for details.
 * 
 * @param <T>
 *            The type (entity) to which expression applies
 * 
 * @author Andrey Potekhin
 * @see Exists0
 */
public class All<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;

    public All(Predicate<? super T> predicate)
    {
        if (predicate == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.predicate = predicate;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        if (condition == null)
        {
            throw new IllegalArgumentException(ARG_CONDITION);
        }
        if (predicate == null)
        {
            throw new IllegalArgumentException(ARG_PREDICATE);
        }
        this.condition = condition;
        this.predicate = predicate;
    }

    public Object evaluate(T t) throws ExistentialException
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
