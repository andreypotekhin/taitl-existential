package com.taitl.existential.expressions;

import com.taitl.existential.exceptions.ExistentialException;

/**
 * Base interface for validation expressions, such as All<T>, Exists<T>.
 *
 * @param <T>
 *            Type of event
 */
public interface Expression<T>
{
    Object evaluate(T t) throws ExistentialException;
}
