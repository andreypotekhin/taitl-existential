package com.taitl.existential.keys;

import com.taitl.existential.configs.*;
import com.taitl.existential.paths.*;

/**
 * A path-like representation of a business operation, serving as a key for finding
 * all matching Contexts (op Context, its parent Contexts and matching wildcard Contexts).
 *
 * Op key cannot be a single slash (/), cannot end with a slash.
 * Wildcard character (*) is not allowed in operation key.
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
