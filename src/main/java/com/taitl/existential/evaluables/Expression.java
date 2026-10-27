package com.taitl.existential.evaluables;

import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;

/**
 * Base interface for entity expressions, such as All<T>.
 * Bare expressions in invariants apply to each matching reported entity/access event during validation.
 * Transitions use the after-value; deletion uses the before-value.
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
    Object evaluate(T t);

    /**
     * Evaluates with the active handle; legacy expressions retain their existing evaluation.
     * Managed sources resolve transaction-local contents through this explicit argument.
     */
    default Object evaluate(T entity, com.taitl.existential.transactions.Tr tr)
    {
        return evaluate(entity);
    }

    /**
     * Returns a short, human-readable description of this expression.
     */
    String description();
}
