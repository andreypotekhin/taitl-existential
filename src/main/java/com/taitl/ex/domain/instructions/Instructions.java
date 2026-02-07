package com.taitl.ex.domain.instructions;

import java.util.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Container for EventHandlers, such as OnRead<T1>, OnUpdate<T2>.
 * // TODO: implements Evs<?>
 */
public class Instructions
{
    // TODO:refactor to List<Ev>
    protected List<Instruction<?>> instructions = new ArrayList<>();

    public <T> Instructions add(Instruction<T> instruction)
    {
        sane(instruction, "instruction");
        instructions.add(instruction);
        return this;
    }

    // TODO:refactor to Ev<T>
    public <T> Instructions add(EventHandler<T> eh)
    {
        sane(eh, "eh");
        instructions.add(new Instruction<>(eh));
        return this;
    }

    public <T> Instructions addAll(Instructions other)
    {
        sane(other, "other");
        for (Instruction<?> instruction : other.instructions)
        {
            instructions.add(instruction);
        }
        return this;
    }

    public boolean isEmpty()
    {
        return instructions.isEmpty();
    }
}
