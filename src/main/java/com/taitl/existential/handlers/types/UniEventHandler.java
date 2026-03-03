package com.taitl.existential.handlers.types;

import com.taitl.existential.exceptions.*;

/**
 * Base interface for event handlers with side effects, such as OnUpdate<T>.
 *
 * Here, E is one of Create, Update, Delete, Read, Write, CU, UD, CUD.
 *
 * @param <T>
 *            Type of entity
 * @see BiEventHandler
 */
public interface UniEventHandler<T> extends EventHandler<T>
{
    void handle(T t) throws ExistentialException;
}
