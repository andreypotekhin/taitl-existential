package com.taitl.existential.interfaces;

import com.taitl.existential.effects.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.invariants.*;
import com.taitl.existential.intents.*;

/**
 * Contract for configuration containers, such as Context and Transaction.
 * Implementations expose a minimal surface for attaching operational metadata
 * and validation or execution hooks.
 */
public interface Configurable
{
    /**
     * Attaches an operation label for diagnostics or runtime behavior selection.
     *
     * @param op
     *            Operation label
     */
    void op(String op);

    /**
     * Registers a transactional invariant to be validated.
     *
     * @param invariant
     *            Invariant to register
     * @param <T>
     *            Type of entity validated by the invariant
     */
    <T> void invariant(Invariant<T> invariant);

    /**
     * Registers a side-effect that should be invoked by the runtime.
     *
     * @param effect
     *            Effect to register
     * @param <T>
     *            Type of entity processed by the effect
     */
    <T> void effect(Effect<T> effect);

    /**
     * Registers an intent that authorizes event emission for a type.
     *
     * @param intent
     *            Intent to register
     * @param <T>
     *            Type of entity governed by the intent
     */
    <T> void intent(Intent<T> intent);

    // TODO: on/off(int flag);

    /**
     * Adds a batch of evaluables to the configuration.
     *
     * @param ev
     *            Collection of evaluables
     * @param <T>
     *            Type of entity processed by the evaluables
     */
    <T> void add(Evs<T> ev);
}
