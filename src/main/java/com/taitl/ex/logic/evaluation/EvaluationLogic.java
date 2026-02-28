package com.taitl.ex.logic.evaluation;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.actions.*;
import com.taitl.ex.logic.evaluation.intents.actions.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluationLogic implements Closeable
{
    protected TransactionLogic tl;
    protected EvaluateIntents evaluateIntents;

    public EvaluationLogic(TransactionLogic tl)
    {
        this.tl = tl;
        this.evaluateIntents = new EvaluateIntents(this);
    }

    /**
     * Evaluate validation expressions and call event handlers.
     * Write any violations to ValidationReport.
     */
    public void evaluate(Tr tr, ValidationReport report) throws ExistentialException
    {
        sane(tr, "tr", report, "report");
        EventField eventField = eventField(tr);
        EvaluateEvent evaluateEvent = Creator.create(EvaluateEvent.class);
        boolean useFullClassNames = useFullClassNames();
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            evaluateEvent.call(runtimeKey, eventField, report, useFullClassNames);
        }
    }

    public void close()
    {
    }

    public <T> void evaluateIntent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(tr, "tr", runtimeKey, "runtimeKey");
        evaluateIntents.call(runtimeKey, tr);
    }

    public Config config(Tr tr)
    {
        sane(tr, "tr");
        Config config = tl.ex().configs().config(tr.op);
        sane(config, "config");
        return config;
    }

    protected EventField eventField(Tr tr)
    {
        sane(tr, "tr");
        return config(tr).indexes().eventField();
    }

    public boolean useFullClassNames()
    {
        return tl.ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }
}
