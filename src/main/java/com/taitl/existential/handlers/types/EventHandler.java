package com.taitl.existential.handlers.types;

import com.taitl.existential.interfaces.Describable;
import com.taitl.existential.rules.Rule;

/**
 * Base interface for event handlers with side effects, such as OnUpdate<Entity>, OnMutate<Entity>.
 *
 * @param <T>
 *            Type of entity
 */
public interface EventHandler<T> extends Rule<T>, Describable
{
}
