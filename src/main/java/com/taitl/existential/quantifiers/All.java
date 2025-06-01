package com.taitl.existential.quantifiers;

import static com.taitl.existential.constants.Strings.ARG_CONDITION;
import static com.taitl.existential.constants.Strings.ARG_PREDICATE;

import java.util.function.Predicate;

import com.taitl.existential.exceptions.ExistentialException;
import com.taitl.existential.exceptions.PredicateFailureException;
import com.taitl.existential.expressions.*;
import com.taitl.existential.helper.Args;

/**
 * Implements "For Any" (universal quantification) notation for reasoning about application entities.
 * See library documentation for details.
 *
 * @param <T>
 *            Entity type T to which the expression applies, or a mutation of an entity type
 *            (Mutation<T>, Transition<T>)
 *
 * @author Andrey Potekhin
 */
public class All<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;
    String description = null;

    public All(Predicate<? super T> predicate)
    {
        Args.cool(predicate, "predicate");
        this.predicate = predicate;
    }

    public All(Predicate<? super T> predicate, String description)
    {
        Args.cool(predicate, "predicate", description, "description");
        this.predicate = predicate;
        this.description = description;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        Args.cool(condition, "condition", predicate, "predicate");
        this.condition = condition;
        this.predicate = predicate;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        Args.cool(condition, "condition", predicate, "predicate", description, "description");
        this.condition = condition;
        this.predicate = predicate;
        this.description = description;
    }

    public Object evaluate(T t) throws ExistentialException
    {
        if (condition == null || condition.test(t))
        {
            if (!predicate.test(t))
            {
                throw new PredicateFailureException(description());
            }
        }
        return null;
    }

    public String description()
    {
        return description == null ? "" : description;
    }
}
