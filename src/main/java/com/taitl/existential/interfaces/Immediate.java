package com.taitl.existential.interfaces;

/**
 * Marker interface for the rules that should execute immediately upon receiving an event.
 * Known implementors: Effect<Entity>, Trancycle<Transaction>
 *
 * @param <T>
 *            Type of entity
 */
public interface Immediate<T>
{
}
