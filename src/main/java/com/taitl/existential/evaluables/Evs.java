package com.taitl.existential.evaluables;

import java.util.*;

/**
 * Marker interface for a list of evaluated statements (Evs) on a single type.
 * Known implementors: Invariant<Entity>, Effect<Entity>, Trancycle<Transaction>.
 *
 * @param <T> Entity type
 */
public interface Evs<T> extends Ev<T>
{
    List<Ev<T>> list();

    Evs<T> add(Ev<T> ev);

    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
