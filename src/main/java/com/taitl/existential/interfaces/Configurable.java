package com.taitl.existential.interfaces;

import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;

public interface Configurable
{
    void op(String op);

    <T> void invariant(Invariant<T> invariant);

    <T> void effect(Effect<T> effect);

    // TODO: allow/deny(Intent<T> intent);
    // TODO: on/off(int flag);

    <T> void add(Evs<T> ev);
}
