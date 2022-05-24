package com.taitl.existential;

import com.taitl.existential.invariants.Invariants;
import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handler.types.EventHandler;

public interface Configurable
{
    <T> Configurable add(EventHandler<T> eh);

    <T> Configurable add(Expression<T> expr);

    <T> void require(Invariants<T> invariants);

    // TODO: intents(Intents<T> intents);
}
