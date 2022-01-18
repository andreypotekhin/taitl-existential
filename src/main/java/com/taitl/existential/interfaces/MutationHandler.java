package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.ExistentialException;

/**
 * Base interface for BiEvent<T> handlers with side effects, such as OnMutate<T>.
 * 
 * Here, E is one of events: Mutate, Permutate.
 *
 * @param <T>
 *            Type of event
 * @see EntityHandler
 */
public interface MutationHandler<T> extends EventHandler<T>
{
    void handle(T t0, T t1) throws ExistentialException;
}
