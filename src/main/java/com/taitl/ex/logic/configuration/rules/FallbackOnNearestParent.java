package com.taitl.ex.logic.configuration.rules;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

public class FallbackOnNearestParent
{
    protected ConfigRegistry registry;

    public FallbackOnNearestParent(ConfigRegistry registry)
    {
        this.registry = registry;
    }

    public Config call(String op)
    {
        sane(op, "op");
        if (!registry.has(op))
        {
            return null;
        }
        return registry.get(op);
    }

    protected boolean matchesOrParent(String op, String candidate)
    {
        sane(op, "op", candidate, "candidate");
        return MatchParentName.matches(op, candidate);
    }

}
