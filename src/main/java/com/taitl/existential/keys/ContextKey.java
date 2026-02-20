package com.taitl.existential.keys;

import com.taitl.existential.configs.*;
import com.taitl.existential.paths.*;

/**
 * A path-like representation of a business operation, serving as a key for
 * configuration and lookup of contexts, including wildcard and parent contexts.
 *
 * Example:
 * {@code /app/orders/update}
 * {@code /app/orders} - parent context of the above context
 * {@code /} - root context (parent to all contexts)
 * {@code /app/*}{@code /update} - wildcard context
 *
 * A context key cannot end with a slash.
 * The wildcard character ({@code *}) is allowed in a context key.
 *
 * @see AbstractPath
 * @see Context
 * @see Transaction
 */
public class ContextKey extends AbstractPath
{
    public ContextKey(String name)
    {
        super(name);
    }

    public static ContextKey from(String name)
    {
        return new ContextKey(name);
    }
}
