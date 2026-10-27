package com.taitl.existential.quantifiers;

import java.util.function.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.transactions.*;
import com.taitl.ex.common.helper.strings.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Implements "For Any" (universal quantification) notation for reasoning about application entities.
 * Evaluates configured predicates against each matching reported subject at the selected stage.
 * Transaction-aware predicates and nested managed Exists sources receive the active Tr explicitly;
 * shared rule instances never retain one handle's mutable index view. Conditional forms skip the
 * predicate when their condition is false, and ordinary failures preserve the configured description.
 *
 * @param <T>
 *            Entity type to which the expression applies, or a mutation of an entity type
 *            (Transition<T>, Porting<T>)
 */
public class All<T> implements Expression<T>
{
    ConcreteAll<T> concrete;
    BiPredicate<? super T, Tr> boundPredicate;
    String boundDescription;

    public All(Predicate<? super T> predicate)
    {
        sane(predicate, "predicate");
        concrete = createBuilder()
                .predicate(predicate)
                .build();
    }

    public All(Predicate<? super T> predicate, String description)
    {
        sane(predicate, "predicate");
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
        sane(condition, "condition", predicate, "predicate");
        concrete = createBuilder()
                .condition(condition)
                .predicate(predicate)
                .description(description)
                .build();
    }

    /** Builds a predicate with explicit access to invocation-owned managed indexes. */
    public All(BiPredicate<? super T, Tr> predicate, String description)
    {
        sane(predicate, "predicate");
        boundPredicate = predicate;
        boundDescription = description;
    }

    public Object evaluate(T entity, Tr tr)
    {
        if (boundPredicate == null)
        {
            return concrete.evaluate(entity, tr);
        }
        if (!boundPredicate.test(entity, tr))
        {
            throw new PredicateFailure(description() + " See " + PredicateFailure.TROUBLESHOOTING_SECTION);
        }
        return null;
    }

    public Object evaluate(T entity)
    {
        if (boundPredicate != null)
        {
            throw new IllegalStateException("Managed All requires explicit transaction evaluation");
        }
        return concrete.evaluate(entity);
    }

    public String description()
    {
        return concrete == null ? Descriptions.text(boundDescription) : concrete.description();
    }

    @SuppressWarnings("unchecked")
    ConcreteAllBuilder<T> createBuilder()
    {
        return Creator.create(ConcreteAllBuilder.class);
    }
}
