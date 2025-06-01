package com.taitl.existential.interfaces;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handlers.types.EventHandler;
import com.taitl.existential.effects.Effect;
import com.taitl.existential.invariants.Invariant;

public interface Configurable
{
    void name(String name);

    <T> void enforce(Invariant<T> invariant);

    <T> void cause(Effect<T> effect);

    <T> Configurable add(EventHandler<T> eh);

    <T> Configurable add(Expression<T> expr);

    // TODO: allow/deny(Intent<T> intent);
}
