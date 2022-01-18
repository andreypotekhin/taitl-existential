package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.ExistentialException;

/**
 * Base interface for Event handlers with side effects, such as On[E]<T>.
 * 
 * Here, E is one of Create, Update, Upsert, Delete, Read.
 *
 * @param <T>
 *            Type of event
 * @see MutationHandler
 */
public interface EntityHandler<T> extends EventHandler<T>
{
    void handle(T t) throws ExistentialException;
}
