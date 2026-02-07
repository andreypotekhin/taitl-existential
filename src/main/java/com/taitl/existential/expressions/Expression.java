package com.taitl.existential.expressions;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;

/**
 * Base interface for entity expressions, such as All<T>.
 *
 * @param <T>
 *            Type of entity
 */
public interface Expression<T> extends Ev<T>
{
    Object evaluate(T t) throws ExistentialException;

    String description();
}
