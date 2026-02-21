package com.taitl.existential.evaluables;

import java.util.*;

/**
 * Marker interface for a list of evaluated statements (Evs) on multiple types.
 * Because this interface models multiple entity types, it is intentionally not generic.
 * Known implementors include Config and Transaction.
 */
public interface Evaluable
{
    /**
     * Returns the list of evaluable rule sets associated with this instance.
     *
     * @return Ordered list of evaluable rule sets
     */
    List<Evs<?>> evs();

    /**
     * Accepts an evaluator visitor for walking the contained rule sets.
     *
     * @param evaluator
     *            Evaluator implementation to visit this instance
     */
    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
