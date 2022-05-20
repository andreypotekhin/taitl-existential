package com.taitl.existential;

import static com.taitl.existential.constants.Strings.ARG_OP;
import static com.taitl.existential.constants.Strings.ARG_OP_FORMAT;

import com.taitl.existential.transactions.Transaction;

/**
 * A path-like representation of a business operation, serving as a key for finding
 * the appropriate Context as well as all its parent Contexts.
 * <p>
 * Example: "/app/orders/update"
 * <p>
 * @author Andrey Potekhin
 * @see Context
 * @see Transaction
 */
public class OpKey
{
    public static final String ARG_OP_SINGLE_SLASH = "Argument 'op' cannot be a signle slash ('/')";
    public static final String ARG_OP_END_SLASH = "Argument 'op' cannot end with a slash ('/')";
    public static final String ARG_OP_NO_WILDCARDS = "Argument 'op' cannot have wildcards ('*')";

    protected static final String SLASH = "/";
    protected static final String WILDCARD = "*";

    protected final String op;

    public OpKey(String op)
    {
        if (op == null)
        {
            throw new IllegalArgumentException(ARG_OP);
        }
        if (!op.startsWith(SLASH))
        {
            throw new IllegalArgumentException(ARG_OP_FORMAT);
        }
        if (SLASH.equals(op))
        {
            throw new IllegalArgumentException(ARG_OP_SINGLE_SLASH);
        }
        if (op.endsWith(SLASH))
        {
            throw new IllegalArgumentException(ARG_OP_END_SLASH);
        }
        if (op.contains(WILDCARD))
        {
            throw new IllegalArgumentException(ARG_OP_NO_WILDCARDS);
        }
        this.op = op;
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
