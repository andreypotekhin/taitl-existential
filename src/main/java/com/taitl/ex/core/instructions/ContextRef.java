package com.taitl.ex.core.instructions;

import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Implements a reference to a set of instructions elsewhere.
 *
 * Used to refer from instructions in OpContext to instructions
 * defined in its custom Contexts.
 *
 * @param <T> Entity type
 */
public class ContextRef<T> extends Instruction<T>
{
    protected Context context;

    public ContextRef(Context context)
    {
        super();
        sane(context, "context");
        this.context = context;
        this.type = InstructionType.REF;
    }
}
