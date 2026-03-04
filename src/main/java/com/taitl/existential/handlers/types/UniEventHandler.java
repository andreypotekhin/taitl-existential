package com.taitl.existential.handlers.types;

import com.taitl.existential.exceptions.*;

/**
 * Base interface for event handler, such as OnUpdate<T>.
 *
 * @param <T>
 *            Type of entity
 * @see BiEventHandler
 */
public interface UniEventHandler<T> extends EventHandler<T>
{
    void handle(T t) throws ExistentialException;
}
