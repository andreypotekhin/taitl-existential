package com.taitl.ex.logic.evaluation;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.*;
import com.taitl.ex.logic.evaluation.intents.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.ex.logic.transactions.*;
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
    protected EvaluateEvents evaluateEvents;
    protected EvaluateIntents evaluateIntents;

    public EvaluationLogic(TransactionLogic tl)
    {
        this.tl = tl;
        this.evaluateEvents = Creator.create(EvaluateEvents.class);
        this.evaluateIntents = Creator.create(EvaluateIntents.class, new Class[] { EvaluationLogic.class }, this);
    }

    public <T> void evaluatePreconditions(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateEvents(runtimeKey, tr, StageName.PRECONDITION, report);
        throwIfStageFailed(StageName.PRECONDITION, report);
    }

    public <T> void evaluateImmediate(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateEvents(runtimeKey, tr, StageName.IMMEDIATE, report);
        throwIfStageFailed(StageName.IMMEDIATE, report);
    }

    public void evaluateValidation(Tr tr, ValidationReport report) throws ExistentialException
    {
        sane(tr, "tr", report, "report");
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            if (hasIntents(tr, StageName.VALIDATION))
            {
                evaluateIntents(runtimeKey, tr, StageName.VALIDATION);
            }
            evaluateEvents(runtimeKey, tr, StageName.VALIDATION, report);
        }
    }

    protected <T> void evaluateEvents(
            RuntimeKey<T> runtimeKey,
            Tr tr,
            StageName stageName,
            ValidationReport report) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr", stageName, "stageName", report, "report");
        EventField eventField = eventField(tr, stageName);
        evaluateEvents.call(runtimeKey, eventField, report, useFullClassNames(), shouldSplitElementary());
    }

    public <T> void evaluateIntents(RuntimeKey<T> runtimeKey, Tr tr, StageName stageName) throws ExistentialException
    {
        sane(stageName, "stageName");
        sane(tr, "tr", runtimeKey, "runtimeKey");
        evaluateIntents.call(runtimeKey, tr, stageName);
    }

    public void close()
    {
    }

    /* Attributes */

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
        return config(tr).indexes(tr.op, StageName.VALIDATION).eventField();
    }

    protected EventField eventField(Tr tr, StageName stageName)
    {
        sane(stageName, "stageName");
        if (stageName == StageName.VALIDATION)
        {
            return eventField(tr);
        }
        return config(tr).indexes(tr.op, stageName).eventField();
    }

    public boolean useFullClassNames()
    {
        return tl.ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }

    public boolean shouldSplitElementary()
    {
        return !tl.ex().get(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
    }

    protected void throwIfStageFailed(StageName stageName, ValidationReport report) throws ExistentialException
    {
        sane(stageName, "stageName", report, "report");
        if (report.isEmpty())
        {
            return;
        }
        throw new ExistentialExceptions(stageName.label(),
                report.exceptions().toArray(new ExistentialException[] {}));
    }

    protected boolean hasIntents(Tr tr, StageName stageName)
    {
        sane(tr, "tr", stageName, "stageName");
        if (tr.hasIntents(stageName))
        {
            return true;
        }
        return config(tr).indexes(tr.op, stageName).hasIntents();
    }
}
