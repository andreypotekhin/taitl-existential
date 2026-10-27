package com.taitl.ex.logic.managed_indexes.rules;

import com.taitl.existential.*;

/** An explicit manual policy bypasses all automatic routing; read enrollment remains independent. */
public class RequiresIndexMaintenance
{
    public boolean in(IndexingPolicy policy, boolean read)
    {
        return policy.automatic() && (!read || policy.enrollReads());
    }
}
