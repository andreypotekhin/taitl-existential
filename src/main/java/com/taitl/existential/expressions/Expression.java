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
    /**
     * Evaluates the expression against the provided entity.
     *
     * @param t Entity instance to evaluate
     * @return Evaluation result (often boolean for predicates)
     * @throws ExistentialException If evaluation fails
     */
    Object evaluate(T t) throws ExistentialException;

    /**
     * Returns a short, human-readable description of this expression.
     */
    String description();
}
