package com.taitl.existential.evaluables;

import com.taitl.existential.interfaces.*;

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

    default void visit(Evaluatable evaluatable)
    {
        for (Evs<?> evs : evaluatable.evs())
        {
            evs.accept(this);
        }
    }
}
