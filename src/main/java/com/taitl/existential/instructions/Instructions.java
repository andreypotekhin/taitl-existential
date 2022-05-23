package com.taitl.existential.instructions;

import java.util.ArrayList;
import java.util.List;

import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.helper.Args;

/**
 * Container for EventHandlers, such as OnRead<T1>, OnUpdate<T2>.
 */
public class Instructions
{
    protected List<EventHandler<?>> instructions = new ArrayList<>();

    public <T> Instructions add(EventHandler<T> eh)
    {
        Args.cool(eh, "eh");
        instructions.add(eh);
        return this;
    }

    public <T> Instructions addAll(Instructions other)
    {
        Args.cool(other, "other");
        for (EventHandler<?> instruction : other.instructions)
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
