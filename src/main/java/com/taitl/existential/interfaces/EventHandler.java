package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.FailureException;

/**
 * Base interface for event handlers with side effects, such as OnUpdate<Entity>.
 *
 * @param <T>
 *            Type of event
 */
public interface EventHandler<T>
{
    void handle(T t) throws FailureException;
}
