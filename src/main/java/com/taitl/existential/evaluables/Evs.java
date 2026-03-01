package com.taitl.existential.evaluables;

import com.taitl.existential.keys.*;

import java.util.*;

/**
 * Collection of evaluable statements (Evs) bound to a single type.
 * Known implementors include Invariant<Entity>, Effect<Entity>, Life<Transaction>.
 *
 * @param <T> Entity type
 */
public interface Evs<T> extends Ev<T>
{
    List<Ev<T>> list();

    Evs<T> add(Ev<T> ev);

    TypeKey<T> typeKey();

    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }

    /**
     * Indicates whether this is a single statement rather than a list.
     */
    default boolean single()
    {
        return false;
    }
}
