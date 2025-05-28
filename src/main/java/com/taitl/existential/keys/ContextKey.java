package com.taitl.existential.keys;

import com.taitl.existential.helper.Args;

import static com.taitl.existential.constants.Strings.*;

import com.taitl.existential.transactions.Transaction;

/**
 * A path-like representation of a business operation, serving as a key for finding
 * appropriate Contexts, including wildcard and parent Contexts.
 *
 * Example:
 * "/app/orders/update"
 * "/app/orders" - parent context of the above context
 * "/" - root context (parent to all contexts)
 * "/app/* /update" - wildcard context
 *
 * Context key cannot end with a slash.
 * Wildcard character (*) is allowed in context key.
 *
 * @see Context
 * @see Transaction
 */
public class ContextKey
{
    public final String name;

    public ContextKey(String name)
    {
        Args.cool(name, "name");
        requireValidName(name);
        this.name = name.trim();
    }

    public String toString()
    {
        return name;
    }

    public static ContextKey valueOf(String s)
    {
        Args.cool(s, "s");
        return new ContextKey(s);
    }

    public boolean hasParent()
    {
        return name.lastIndexOf(SLASH) != 0;
    }

    public boolean isWildcard()
    {
        return name.contains(WILDCARD);
    }

    public static void requireValidName(String name)
    {
        Args.cool(name, "name");
        name = name.trim();
        Args.require(name.startsWith(SLASH), "Argument 'name' should start with a slash ('/')");
        Args.require(name.length() == 1 || !name.endsWith(SLASH), "Argument 'name' should not end with a slash ('/')");
    }

    /**
     * Gets this key parent key, if any - a shortened key without the part starting with the last slash.
     * Throws IllegalStateException if this key is a top-level key (has no parent).
     * <p>
     * Example:
     *   Key: "/app/orders/update"
     *   Parent key: "/app/orders"
     * <p>
     * @return A shortened key without the part starting with the last slash.
     * @throws IllegalStateException if this key is a top-level key (has no parent).
     */
    public ContextKey getParent()
    {
        if (!hasParent())
        {
            throw new IllegalStateException(String.format("ContextKey '%s' has no parent key", name));
        }
        return new ContextKey(name.substring(0, name.lastIndexOf(SLASH)));
    }

    public int hashCode()
    {
        return name.hashCode();
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (other == null)
        {
            return false;
        }
        if (!(other instanceof ContextKey))
        {
            return false;
        }
        ContextKey o = (ContextKey) other;
        if (o.name == null)
        {
            return (this.name == null);
        }
        return o.name.equals(this.name);
    }
}
