package com.taitl.existential.paths;

import com.taitl.existential.contexts.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.existential.constants.Strings.*;

/**
 * Path-like representation of a business operation, serving as a key for finding
 * appropriate Contexts, including wildcard and parent Contexts.
 *
 * Example:
 * "/app/orders/update"
 * "/app/orders" - parent context
 * "/" - root context (parent to all contexts)
 * "/app/*a/update" - wildcard context
 *
 * AbstractPath key cannot end with a slash.
 * Wildcard character (*) is allowed in context key.
 *
 * @see Context
 * @see Transaction
 *
 * Usage:
 * @see ContextKey
 */
public class AbstractPath
{
    public final String path;

    public AbstractPath(String path)
    {
        sane(path, "name");
        validate(path);
        this.path = path.trim();
    }

    public String toString()
    {
        return path;
    }

    public static AbstractPath valueOf(String s)
    {
        sane(s, "s");
        return new AbstractPath(s);
    }

    public boolean hasParent()
    {
        return path.lastIndexOf(SLASH) != 0;
    }

    public boolean isWildcard()
    {
        return path.contains(WILDCARD);
    }

    public static void validate(String name)
    {
        sane(name, "name");
        name = name.trim();
        check(name.startsWith(SLASH), "Argument 'name' should start with a slash ('/')");
        check(name.length() == 1 || !name.endsWith(SLASH),
                "Argument 'name' should not end with a slash ('/')");
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
    public AbstractPath getParent()
    {
        if (!hasParent())
        {
            throw new IllegalStateException(String.format("ContextKey '%s' has no parent key", path));
        }
        return new AbstractPath(path.substring(0, path.lastIndexOf(SLASH)));
    }

    public int hashCode()
    {
        return path.hashCode();
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
        if (!(other instanceof AbstractPath))
        {
            return false;
        }
        AbstractPath o = (AbstractPath) other;
        if (o.path == null)
        {
            return (this.path == null);
        }
        return o.path.equals(this.path);
    }
}
