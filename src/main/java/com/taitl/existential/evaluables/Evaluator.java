package com.taitl.existential.evaluables;

/**
 * Evaluates Evs and Evaluable containers by visiting their rule sets.
 */
public interface Evaluator
{
    /**
     * Visits a single evaluated statement.
     *
     * @param ev
     *            Evaluated statement to visit
     * @param <T>
     *            Type of entity associated with the statement
     */
    <T> void visit(Ev<T> ev);

    /**
     * Visits an Evs container by traversing its elements.
     *
     * @param evs
     *            Rule set to traverse
     * @param <T>
     *            Type of entity associated with the rule set
     */
    default <T> void visit(Evs<T> evs)
    {
        for (Ev<?> ev : evs.list())
        {
            ev.accept(this);
        }
    }

    /**
     * Visits an Evaluable container by traversing its rule sets.
     *
     * @param evaluable
     *            Container with rule sets to traverse
     */
    default void visit(Evaluable evaluable)
    {
        for (Evs<?> evs : evaluable.evs())
        {
            evs.accept(this);
        }
    }
}
