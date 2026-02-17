package com.taitl.existential.keys;

import com.taitl.existential.configs.*;
import com.taitl.existential.paths.*;

/**
 * A path-like representation of a business operation, serving as a key for
 * configuring and lookup of Contexts, including wildcard and parent Contexts.
 *
 * Example:
 * "/app/orders/update"
 * "/app/orders" - parent context of the above context
 * "/" - root context (parent to all contexts)
 * "/app/* /update" - wildcard context
 *
 * Context key cannot end with a slash.
 * The wildcard character (*) is allowed in context key.
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
