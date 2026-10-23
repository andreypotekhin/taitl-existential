package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Resolves the ordered contexts applicable to one operation path.
 * Configuration indexing and transaction/factory lookup share this delegate, ensuring they
 * use the same parent, wildcard, and concrete-context ordering.
 *
 * RuleOrderingLogic owns the ordering policy. Resolution returns a fresh list without
 * modifying the configured contexts or retaining a transaction-specific result.
 */
public class ContextResolutionLogic
{
    @Logic
    protected RuleOrderingLogic ruleOrderingLogic;

    public ContextResolutionLogic()
    {
        ruleOrderingLogic = Creator.create(RuleOrderingLogic.class);
    }

    public List<Context> resolve(String op, List<Context> contexts)
    {
        sane(op, "op", contexts, "contexts");
        return ruleOrderingLogic.order(op, contexts);
    }
}
