package com.taitl.existential.evaluables;

import java.util.*;

/**
 * Marker interface for a list of evaluables (Evs) on a type, e.g. entity class.
 * Known implementors: Invariant<Entity>, Effect<Entity>, Trancycle<Transaction>.
 *
 * @param <T> Type parameter
 */
public interface Evs<T> extends Ev<T>
{
    List<Ev<T>> list();

    default void accept(Evaluator evaluator)
    {
        for (Ev<T> ev : list())
        {
            ev.accept(evaluator);
        }
    }
}
