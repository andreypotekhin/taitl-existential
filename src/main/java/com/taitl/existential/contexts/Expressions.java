package com.taitl.existential.contexts;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.helper.Args;

/**
 * Container for Expressions, such as All<T1>, All<T2>.
 */
public class Expressions
{
    public <T> Expressions add(Expression<T> expr)
    {
        Args.cool(expr, "expr");
        // TODO: add expression
        return this;
    }

    public <T> Expressions addAll(Expressions other)
    {
        Args.cool(other, "other");
        // TODO: add event handler
        return this;
    }
}
