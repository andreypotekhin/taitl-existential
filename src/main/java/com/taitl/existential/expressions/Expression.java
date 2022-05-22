package com.taitl.existential.expressions;

import com.taitl.existential.rules.Rule;
import com.taitl.existential.exceptions.ExistentialException;

/**
 * Base interface for entity expressions, such as All<T>.
 *
 * @param <T>
 *            Type of entity
 */
public interface Expression<T> extends Rule<T>
{
    Object evaluate(T t) throws ExistentialException;
}
