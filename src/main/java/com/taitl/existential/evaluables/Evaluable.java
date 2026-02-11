package com.taitl.existential.evaluables;

import java.util.*;

/**
 * Marker interface for a list of evaluated statements (Evs) on multiple types.
 * Because of working with multiple types, this interface is not generic.
 * Known implementors: Config, Transaction
 */
public interface Evaluable
{
    List<Evs<?>> evs();

    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
