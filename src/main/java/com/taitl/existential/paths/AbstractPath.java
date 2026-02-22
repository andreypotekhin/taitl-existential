package com.taitl.existential.paths;

import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Text.*;
import static com.taitl.existential.constants.Strings.*;

/**
 * Path-like representation of a business operation, serving as a key for finding
 * appropriate contexts, including wildcard and parent contexts.
 *
 * Example:
 * {@code /app/orders/update}
 * {@code /app/orders} - parent context
 * {@code /} - root context (parent to all contexts)
 * {@code /app/*a/update} - wildcard context
 *
 * An {@link AbstractPath} cannot end with a slash.
 * The wildcard character ({@code *}) is allowed in a context key.
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
        this.path = trimmed(path, "name");
    }

    @Override
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
        name = trimmed(name, "name");
        check(name.startsWith(SLASH), "Argument 'name' should start with a slash ('/')");
        check(name.length() == 1 || !name.endsWith(SLASH),
                "Argument 'name' should not end with a slash ('/')");
    }

    /**
     * Gets this key's parent key, if any, without the part starting at the last slash.
     * Throws {@link IllegalStateException} if this key is a top-level key (has no parent).
     *
     * Example:
     * Key: {@code /app/orders/update}
     * Parent key: {@code /app/orders}
     *
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

    @Override
    public int hashCode()
    {
        return path.hashCode();
    }

    @Override
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
