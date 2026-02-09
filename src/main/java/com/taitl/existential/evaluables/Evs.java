package com.taitl.existential.evaluables;

import java.util.*;

/**
 * Marker interface for a list of evaluated statements (Evs) on a type, e.g. an Entity.
 * Example implementors: Invariant<Entity>, Effect<Entity>, Trancycle<Transaction>.
 * @param <T> Type parameter
 */
public interface Evs<T> extends Ev<T>
{
    List<Ev<T>> list();

    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
