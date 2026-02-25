package com.taitl.existential.paths;

import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;
import com.taitl.ex.common.helper.PathSupport;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Text.*;

/**
 * Path-like representation of a business operation, serving as a key for locating
 * matching contexts, including wildcard and parent contexts.
 *
 * Examples:
 * /app/orders/update
 * /app/orders - parent context
 * / - root context (parent to all contexts)
 * /app/*a/update - wildcard context
 *
 * An AbstractPath cannot end with a slash.
 * The wildcard character (*) is allowed in a context key.
 *
 * @see Context
 * @see Transaction
 * @see ContextKey
 */
public class AbstractPath
{
    public final String path;

    public AbstractPath(String path)
    {
        validate(path);
        this.path = trimmed(path, "path");
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
        return PathSupport.hasParent(path);
    }

    public boolean isWildcard()
    {
        return path.contains("*");
    }

    public static void validate(String path)
    {
        path = trimmed(path, "path");
        check(path.startsWith("/"), "Argument 'path' should start with a slash ('/')");
        check(path.length() == 1 || !path.endsWith("/"),
                "Argument 'path' should not end with a slash ('/')");
    }

    /**
     * Gets this context key's parent key, if any, without the part starting at the last slash.
     * Throws {@link IllegalStateException} if this context key is a top-level key (has no parent).
     *
     * Example:
     * Key: /app/orders/update
     * Parent key: /app/orders
     *
     * @return A shortened key without the part starting with the last slash.
     * @throws IllegalStateException if this context key is a top-level key (has no parent).
     */
    public AbstractPath getParent()
    {
        return new AbstractPath(PathSupport.parentOrThrow(path, "Context key"));
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
        if (!(other instanceof AbstractPath))
        {
            return false;
        }
        AbstractPath o = (AbstractPath) other;
        return o.path.equals(this.path);
    }
}
