package com.taitl.existential.quantifiers;

import java.util.function.*;
import com.taitl.ex.common.helper.strings.Descriptions;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.expressions.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Implements "For Any" (universal quantification) notation for reasoning about application entities.
 * See library documentation for details.
 *
 * @param <T>
 *            Entity type to which the expression applies, or a mutation of an entity type
 *            (Mutation<T>, Transition<T>)
 */
// TODO: Delegate to ConcreteAll
public class All<T> implements Expression<T>
{
    Predicate<? super T> condition;
    Predicate<? super T> predicate;
    String description = null;

    public All(Predicate<? super T> predicate)
    {
        sane(predicate, "predicate");
        this.predicate = predicate;
    }

    public All(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate", description, "description");
        this.predicate = predicate;
        this.description = description;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        sane(condition, "condition", predicate, "predicate");
        this.condition = condition;
        this.predicate = predicate;
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        sane(condition, "condition", predicate, "predicate", description, "description");
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
                throw new PredicateFailure(description());
            }
        }
        return null;
    }

    public String description()
    {
        return Descriptions.text(description);
    }
}
