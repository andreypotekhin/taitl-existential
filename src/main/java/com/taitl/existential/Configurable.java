package com.taitl.existential;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.invariants.Invariant;

public interface Configurable
{
    // TODO: intent(Intent<T> intent);

    <T> void require(Invariant<T> invariant);

    <T> Configurable add(EventHandler<T> eh);

    <T> Configurable add(Expression<T> expr);
}
