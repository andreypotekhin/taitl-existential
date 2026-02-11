package com.taitl.ex.logic.unused.expressions;

import com.taitl.ex.common.helper.*;
import com.taitl.existential.expressions.*;

/**
 * Container for Expressions, such as All<T1>, All<T2>.
 * // TODO:retire
 */
public class Expressions
{
    public <T> Expressions add(Expression<T> expr)
    {
        Args.sane(expr, "expr");
        // TODO: add expression
        return this;
    }

    public <T> Expressions addAll(Expressions other)
    {
        Args.sane(other, "other");
        // TODO: add event handler
        return this;
    }
}
