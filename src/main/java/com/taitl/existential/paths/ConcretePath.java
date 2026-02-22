package com.taitl.existential.paths;

import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.Text.*;
import static com.taitl.existential.constants.Strings.*;

/**
 * Path-like representation of a business operation, used for identification of
 * the applicable Contexts (operation Context, parent Contexts and any matching wildcard Contexts).
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
        this.op = trimmed(op, "op");
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
        op = trimmed(op, "op");
        check(op.startsWith(SLASH), "Argument 'op' should start with a slash ('/')");
        check(!SLASH.equals(op), ARG_OP_SINGLE_SLASH);
        check(!op.endsWith(SLASH), "Argument 'op' should not end with a slash ('/')");
        check(!op.contains(WILDCARD), ARG_OP_NO_WILDCARDS);
    }

    /**
     * Returns parent key, if any, that is, a shortened key without last part - the part starting at the last slash.
     * Throws IllegalStateException if this key is a top-level key (has no parent).
     * Example:
     *   Key: "/app/orders/update"
     *   Parent key: "/app/orders"
     *
     * @return A shortened key without the part starting with the last slash.
     * @throws IllegalStateException If this key is a top-level key (has no parent).
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
