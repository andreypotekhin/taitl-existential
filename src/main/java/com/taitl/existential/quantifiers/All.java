package com.taitl.existential.quantifiers;

import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
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
public class All<T> implements Expression<T>
{
    ConcreteAll<T> concrete;

    public All(Predicate<? super T> predicate)
    {
        sane(predicate, "predicate");
        concrete = createBuilder()
                .predicate(predicate)
                .build();
    }

    public All(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate", description, "description");
        concrete = createBuilder()
                .predicate(predicate)
                .description(description)
                .build();
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate)
    {
        sane(condition, "condition", predicate, "predicate");
        concrete = createBuilder()
                .condition(condition)
                .predicate(predicate)
                .build();
    }

    public All(Predicate<? super T> condition, Predicate<? super T> predicate, String description)
    {
        sane(condition, "condition", predicate, "predicate", description, "description");
        concrete = createBuilder()
                .condition(condition)
                .predicate(predicate)
                .description(description)
                .build();
    }

    public Object evaluate(T entity) throws ExistentialException
    {
        return concrete.evaluate(entity);
    }

    public String description()
    {
        return concrete.description();
    }

    @SuppressWarnings("unchecked")
    ConcreteAllBuilder<T> createBuilder()
    {
        return Creator.create(ConcreteAllBuilder.class);
    }
}
