package com.taitl.ex.logic.configuration.indexes.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.rules.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.evaluables.*;

import static com.taitl.ex.common.helper.Args.*;

public class IndexConfig
{
    @Up
    protected ConfigurationIndexes ci;

    @Logic
    protected IndexEvs indexEvs;

    public IndexConfig(ConfigurationIndexes ci)
    {
        sane(ci, "ci");
        this.ci = ci;
        this.indexEvs = Creator.create(IndexEvs.class, new Class[] { ConfigurationIndexes.class }, ci);
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
        for (Context context : config.contexts())
        {
            if (!MatchParentName.matches(op, context.name()))
            {
                continue;
            }
            for (Evs<?> evs : context.stage().at(stageName))
            {
                indexEvs.call(evs);
            }
        }
        ci.doneIndexing();
    }
}
