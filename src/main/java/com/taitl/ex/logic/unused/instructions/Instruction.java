package com.taitl.ex.logic.unused.instructions;

import com.taitl.existential.expressions.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;

@Deprecated
public class Instruction<T>
{
    protected enum InstructionType
    {
        EXPRESSION, HANDLER, REF, SPECIAL
    }

    protected EventHandler<T> handler;
    protected Expression<T> expression;
    protected InstructionType type;

    public Instruction(EventHandler<T> handler)
    {
        sane(handler, "handler");
        this.handler = handler;
        this.type = InstructionType.HANDLER;
    }

    public Instruction(Expression<T> expression)
    {
        sane(expression, "expression");
        this.expression = expression;
        this.type = InstructionType.EXPRESSION;
    }

    /**
     * For subclasses.
     * @see TransactionRef
     */
    protected Instruction()
    {
        this.type = InstructionType.SPECIAL;
    }

}
