package com.taitl.existential.evaluables;

/**
 * Marker interface for an evaluated statement.
 * Examples: an expression (All<Entity>), an event handler (OnUpdate<Entity>)
 *
 * @param <T> Entity type
 */
public interface Ev<T>
{
    default void accept(Evaluator evaluator)
    {
        evaluator.visit(this);
    }
}
