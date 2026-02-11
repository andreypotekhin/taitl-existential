package com.taitl.existential.keys;

import com.taitl.existential.contexts.*;
import com.taitl.existential.paths.*;
import com.taitl.existential.transactions.*;

/**
 * A path-like representation of a business operation, serving as a key for finding
 * the matching Contexts (op Context, its parent Contexts, any matching wildcard Contexts).
 *
 * Op key cannot be a single slash (/), cannot end with a slash. Wildcard character (*)
 * is not allowed in op key.
 *
 * Example: "/app/orders/update"
 *
 * @see Context
 * @see Transaction
 */
public class OpKey extends ConcretePath
{
    public OpKey(String op)
    {
        super(op);
    }
}
