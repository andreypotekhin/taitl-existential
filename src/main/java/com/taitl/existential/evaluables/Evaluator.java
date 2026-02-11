package com.taitl.existential.evaluables;

/**
 * Evaluates Evs and Evaluables
 */
public interface Evaluator
{
    <T> void visit(Ev<T> ev);

    default <T> void visit(Evs<T> evs)
    {
        for (Ev<?> ev : evs.list())
        {
            ev.accept(this);
        }
    }

    default void visit(Evaluable evaluable)
    {
        for (Evs<?> evs : evaluable.evs())
        {
            evs.accept(this);
        }
    }
}
