package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

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
