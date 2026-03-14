package com.taitl.existential.keys;

import com.taitl.ex.common.paths.*;
import com.taitl.existential.configs.*;

/**
 * A path-like representation of a business operation, serving as a key for
 * configuration and lookup of contexts, including wildcard and parent contexts.
 *
 * Example:
 * /app/orders/update
 * /app/orders - parent context of the above context
 * / - root context (parent to all contexts)
 * /app/{*}/update - wildcard context
 *
 * A context key cannot end with a slash.
 * The wildcard character (*) is allowed in a context key.
 *
 * @see com.taitl.ex.common.paths.AbstractPath
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
