package com.taitl.existential.handlers.types;

import com.taitl.existential.exceptions.*;

/**
 * Base interface for BiEvent<T> handlers with side effects, such as OnTransit<T>.
 *
 * @param <T>
 *            Type of entity
 * @see UniEventHandler
 */
public interface BiEventHandler<T> extends EventHandler<T>
{
    void handle(T t0, T t1) throws ExistentialException;
}
