package com.taitl.ex.core.instructions;

import java.util.*;
import com.taitl.existential.evaluables.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Container (list) of evaluables, such as OnRead<T1>, OnUpdate<T2>.
 */
public class Instructions
{
    List<Ev<?>> instructions = new ArrayList<>();

    public <T> Instructions add(Ev<T> ev)
    {
        sane(ev, "ev");
        instructions.add(ev);
        return this;
    }

    public <T> Instructions addAll(Evs<T> other)
    {
        sane(other, "other");
        instructions.addAll(other.list());
        return this;
    }

    public <T> Instructions addAll(Instructions other)
    {
        sane(other, "other");
        instructions.addAll(other.instructions);
        return this;
    }

    public boolean isEmpty()
    {
        return instructions.isEmpty();
    }

    public List<Ev<?>> list()
    {
        return instructions;
    }
}
