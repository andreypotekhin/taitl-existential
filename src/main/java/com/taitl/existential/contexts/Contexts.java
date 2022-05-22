package com.taitl.existential.contexts;

import java.util.function.Supplier;

import com.taitl.existential.EventSplitter;
import com.taitl.existential.helper.Args;

public class Contexts
{
    /**
     * EventSplitter factory.
     */
    static Supplier<? extends EventSplitter> eventSplitterFactory = () -> new EventSplitter();

    /**
     * Get context by name. Create new one if it does not exist.
     */
    public Context get(String op)
    {
        throw new RuntimeException("implement me");
    }

    public void eventSplitter(Supplier<? extends EventSplitter> supplier)
    {
        Args.cool(supplier, "supplier");
        eventSplitterFactory = supplier;
    }
}
