package com.taitl.existential.keys;

import static com.taitl.existential.constants.Strings.*;

import com.taitl.existential.helper.Args;
import com.taitl.existential.transactions.Transaction;

/**
 * A path-like representation of a business operation, serving as a key for finding
 * the appropriate Context as well as all its parent Contexts.
 *
 * Op key cannot be a single slash (/), cannot end with a slash. Wildcard character (*)
 * is not allowed in op key.
 * <p>
 * Example: "/app/orders/update"
 * <p>
 *
 * @see Context
 * @see Transaction
 */
public class OpKey
{
    protected final String op;

    public OpKey(String op)
    {
        validate(op);
        this.op = op.trim();
    }

    public String toString()
    {
        return op;
    }

    public static OpKey valueOf(String s)
    {
        return new OpKey(s);
    }

    public boolean hasParent()
    {
        return op.lastIndexOf(SLASH) != 0;
    }

    public boolean isWildcard()
    {
        return op.contains(WILDCARD);
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
    public OpKey getParent()
    {
        if (!hasParent())
        {
            throw new IllegalStateException(String.format("OpKey '%s' has no parent key", op));
        }
        return new OpKey(op.substring(0, op.lastIndexOf(SLASH)));
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
        if (!(other instanceof OpKey))
        {
            return false;
        }
        OpKey o = (OpKey) other;
        if (o.op == null)
        {
            return (this.op == null);
        }
        return o.op.equals(this.op);
    }
}
