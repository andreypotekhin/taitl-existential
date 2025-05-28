package com.taitl.existential.handlers.types;

import com.taitl.existential.exceptions.ExistentialException;

/**
 * Base interface for Event handlers with side effects, such as On[E]<T>.
 *
 * Here, E is one of Create, Update, Upsert, Delete, Read.
 *
 * @param <T>
 *            Type of entity
 * @see BiEventHandlerWithSideEffects
 */
public interface EventHandlerWithSideEffects<T> extends EventHandler<T>
{
    void handle(T t) throws ExistentialException;
}
