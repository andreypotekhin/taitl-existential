package com.taitl.existential;

import com.taitl.existential.expressions.Expression;
import com.taitl.existential.handler.types.EventHandler;
import com.taitl.existential.invariants.Invariants;

public interface Configurable
{
    // TODO: declare(Intents<T> intents);
    
    //<T> void require(Invariants<T> invariants);
    
    <T> void verify(Invariants<T> invariants);
    
    <T> Configurable add(EventHandler<T> eh);

    <T> Configurable add(Expression<T> expr);
}
