package com.taitl.existential.interfaces;

/**
 * Marker interface for the rules that are executed immediately upon receiving an event.
 *
 * Examples: Intent<Entity>, Effect<Entity>.
 *
 * @param <T>
 *            Type of entity
 */
public interface Immediate<T>
{
}
