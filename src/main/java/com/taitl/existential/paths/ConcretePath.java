package com.taitl.existential.paths;

import com.taitl.existential.helper.*;
import com.taitl.existential.transactions.*;

import static com.taitl.existential.constants.Strings.*;

/**
 * A path-like representation of a business operation, serving for identification of
 * the applicable Contexts (operation Context, its parent Contexts, any matching wildcard Contexts).
 *
 * Example: "/app/orders/update"
 *
 * Concrete path key cannot be a single slash (/), nor can it end with a slash.
 * Wildcard character (*) is not allowed in concrete paths.
 *
 * @see Context
 * @see Transaction
 *
 * Usage:
 * @see OpKey
 */
public class ConcretePath
{
    protected final String op;

    public ConcretePath(String op)
    {
        validate(op);
        this.op = op.trim();
    }

    public String toString()
    {
        return op;
    }

    public static ConcretePath valueOf(String s)
    {
        return new ConcretePath(s);
    }

    public boolean hasParent()
    {
        return op.lastIndexOf(SLASH) != 0;
    }

    public static void validate(String op)
    {
        Args.cool(op, "op");
        op = op.trim();
        Args.require(op.startsWith(SLASH), "Argument 'op' should start with a slash ('/')");
        Args.require(!SLASH.equals(op), ARG_OP_SINGLE_SLASH);
        Args.require(!op.endsWith(SLASH), "Argument 'op' should not end with a slash ('/')");
        Args.require(!op.contains(WILDCARD), ARG_OP_NO_WILDCARDS);
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
    public ConcretePath getParent()
    {
        if (!hasParent())
        {
            throw new IllegalStateException(String.format("OpKey '%s' has no parent key", op));
        }
        return new ConcretePath(op.substring(0, op.lastIndexOf(SLASH)));
    }

    public int hashCode()
    {
        return op.hashCode();
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
        if (!(other instanceof ConcretePath))
        {
            return false;
        }
        ConcretePath o = (ConcretePath) other;
        if (o.op == null)
        {
            return (this.op == null);
        }
        return o.op.equals(this.op);
    }
}
