package com.taitl.existential.interfaces;

import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;

public interface Configurable
{
    void name(String name);

    <T> void invariant(Invariant<T> invariant);

    <T> void effect(Effect<T> effect);

    // <T> Configurable add(Ev<T> ev);
    <T> void add(Evs<T> ev);

    // <T> Configurable add(EventHandler<T> eh);
    //
    // <T> Configurable add(Expression<T> expr);

    // TODO: allow/deny(Intent<T> intent);
    // TODO: on/off(int flag);
}
