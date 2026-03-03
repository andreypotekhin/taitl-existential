package com.taitl.ex.logic.configuration.rules;

import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.configs.*;

import java.util.*;

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
        Config closest = null;
        for (Map.Entry<String, Config> entry : registry.configs().entrySet())
        {
            String candidate = entry.getKey();
            if (!matchesOrParent(op, candidate))
            {
                continue;
            }
            if (closest == null || candidate.length() > closest.name().length())
            {
                closest = entry.getValue();
            }
        }
        return closest;
    }

    protected boolean matchesOrParent(String op, String candidate)
    {
        sane(op, "op", candidate, "candidate");
        if (op.equals(candidate))
        {
            return true;
        }
        if ("/".equals(candidate))
        {
            return true;
        }
        return op.startsWith(candidate + "/");
    }
}
