package com.taitl.ex.logic.configuration;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.configs.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class RuleOrderingLogic
{
    @Logic
    protected WildcardContextLogic wildcardContextLogic;

    public RuleOrderingLogic()
    {
        wildcardContextLogic = Creator.create(WildcardContextLogic.class);
    }

    public List<Context> order(String op, List<Context> contexts)
    {
        sane(op, "op", contexts, "contexts");
        List<String> levels = pathLevels(op);
        List<Context> ordered = new ArrayList<>();
        Set<Context> added = Collections.newSetFromMap(new IdentityHashMap<>());
        for (String level : levels)
        {
            for (Context context : contexts)
            {
                if (context.name().contains("*") && wildcardContextLogic.matches(context.name(), level))
                {
                    add(context, added, ordered);
                }
            }
            for (Context context : contexts)
            {
                if (context.name().equals(level))
                {
                    add(context, added, ordered);
                }
            }
        }
        return ordered;
    }

    protected List<String> pathLevels(String op)
    {
        List<String> levels = new ArrayList<>();
        String level = op;
        while (!"/".equals(level))
        {
            levels.add(level);
            level = parentPath(level);
        }
        levels.add("/");
        Collections.reverse(levels);
        return levels;
    }

    protected void add(Context context, Set<Context> added, List<Context> ordered)
    {
        if (added.add(context))
        {
            ordered.add(context);
        }
    }

    protected String parentPath(String path)
    {
        int slash = path.lastIndexOf('/');
        return slash == 0 ? "/" : path.substring(0, slash);
    }
}
