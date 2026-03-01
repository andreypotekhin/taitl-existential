package com.taitl.existential.interfaces;

/**
 * Marker interface for rules that execute immediately upon receiving an event.
 * Known implementors include Effect<Entity> and Life<Transaction>.
 *
 * @param <T>
 *            Type of entity
 */
public interface Immediate<T>
{
}
