package com.taitl.ex.common.paths;

import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;
import com.taitl.ex.common.helper.strings.PathStrings;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.strings.Text.*;

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
    private static final String TROUBLESHOOTING_SECTION = "/Troubleshooting.md#invalid-operation-key";
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
        return PathStrings.hasParent(op);
    }

    public static void validate(String op)
    {
        op = trimmed(op, "op");
        check(op.startsWith("/"), "Operation key should start with a slash ('/')" + " See " + TROUBLESHOOTING_SECTION);
        check(!"/".equals(op), "Operation key cannot be a single slash ('/')" + " See " + TROUBLESHOOTING_SECTION);
        check(!op.endsWith("/"), "Operation key cannot end with a slash ('/')" + " See " + TROUBLESHOOTING_SECTION);
        check(!op.contains("*"), "Operation key cannot have wildcards ('*')" + " See " + TROUBLESHOOTING_SECTION);
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
        return new ConcretePath(PathStrings.parentOrThrow(op, "Operation key"));
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
