package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class IndexConfig
{
    @Up
    protected ConfigurationIndexes ci;

    @Logic
    protected IndexEvs indexEvs;

    @Logic
    protected ContextResolutionLogic contextResolutionLogic;

    @Logic
    protected TypeMatchingLogic typeMatchingLogic;

    public IndexConfig(ConfigurationIndexes ci)
    {
        sane(ci, "ci");
        this.ci = ci;
        this.indexEvs = Creator.create(IndexEvs.class, new Class[] { ConfigurationIndexes.class }, ci);
        this.contextResolutionLogic = Creator.create(ContextResolutionLogic.class);
        this.typeMatchingLogic = Creator.create(TypeMatchingLogic.class);
    }

    public void call(String op, Config config)
    {
        sane(op, "op", config, "config");
        indexConfig(op, config, StageName.VALIDATION);
    }

    public void call(String op, Config config, StageName stageName)
    {
        sane(op, "op", config, "config", stageName, "stageName");
        indexConfig(op, config, stageName);
    }

    /**
     * Add all configured rules to indexes, in the order of declaration.
     */
    public void indexConfig(String op, Config config, StageName stageName)
    {
        sane(op, "op", config, "config", stageName, "stageName");
        List<Context> contexts = contextResolutionLogic.resolve(op, config.contexts());
        validateTypes(contexts);
        for (Context context : contexts)
        {
            for (Evs<?> evs : context.stage().at(stageName))
            {
                indexEvs.call(evs, context.name(), false);
            }
        }
        ci.doneIndexing();
    }

    protected void validateTypes(List<Context> contexts)
    {
        for (Context context : contexts)
        {
            for (StageName stageName : StageName.values())
            {
                for (Evs<?> evs : context.stage().at(stageName))
                {
                    TypeKey<?> typeKey = evs.typeKey();
                    if (typeKey != null)
                    {
                        typeMatchingLogic.validateRuleType(typeKey);
                    }
                }
            }
        }
    }
}
