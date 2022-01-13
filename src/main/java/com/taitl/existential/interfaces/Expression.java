package com.taitl.existential.interfaces;

import com.taitl.existential.exceptions.FailureException;

/**
 * Base interface for validation expressions, such as All<T>, Exists<T>.
 *
 * @param <T>
 *            Type of event
 */
public interface Expression<T>
{
    Object evaluate(T t) throws FailureException;
}
