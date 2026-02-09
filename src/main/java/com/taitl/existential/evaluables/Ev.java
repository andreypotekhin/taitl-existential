package com.taitl.existential.evaluables;

/**
 * Marker interface for an evaluated statement, such as an expression (All<Entity>), event handler (OnUpdate<Entity>),
 * and the like.
 *
 * @param <T>
 *            Type of entity
 */
public interface Ev<T>
{
    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
