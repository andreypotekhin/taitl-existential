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
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            if (hasIntents(tr, StageName.VALIDATION))
            {
                evaluateIntent(runtimeKey, tr, StageName.VALIDATION);
            }
            evaluateStage(runtimeKey, tr, StageName.VALIDATION, report);
        }
    }

    public void close()
    {
    }

    public <T> void evaluateIntent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        evaluateIntent(runtimeKey, tr, StageName.IMMEDIATE);
    }

    public <T> void evaluateIntent(RuntimeKey<T> runtimeKey, Tr tr, StageName stageName) throws ExistentialException
    {
        sane(stageName, "stageName");
        sane(tr, "tr", runtimeKey, "runtimeKey");
        evaluateIntents.call(runtimeKey, tr, stageName);
    }

    public <T> void evaluatePrecondition(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateStage(runtimeKey, tr, StageName.PRECONDITION, report);
        throwIfStageFailed(StageName.PRECONDITION, report);
    }

    public <T> void evaluateImmediate(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateStage(runtimeKey, tr, StageName.IMMEDIATE, report);
        throwIfStageFailed(StageName.IMMEDIATE, report);
    }

    public Config config(Tr tr)
    {
        sane(tr, "tr");
        Config config = tl.ex().configs().config(tr.op);
        sane(config, "config");
        return config;
    }

    protected boolean hasConfig(Tr tr)
    {
        sane(tr, "tr");
        try
        {
            tl.ex().configs().config(tr.op);
            return true;
        }
        catch (RuntimeException ex)
        {
            return false;
        }
    }

    protected EventField eventField(Tr tr)
    {
        sane(tr, "tr");
        return config(tr).indexes(StageName.VALIDATION).eventField();
    }

    protected EventField eventField(Tr tr, StageName stageName)
    {
        sane(stageName, "stageName");
        if (stageName == StageName.VALIDATION)
        {
            return eventField(tr);
        }
        return config(tr).indexes(stageName).eventField();
    }

    public boolean useFullClassNames()
    {
        return tl.ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }

    protected <T> void evaluateStage(
            RuntimeKey<T> runtimeKey,
            Tr tr,
            StageName stageName,
            ValidationReport report) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr", stageName, "stageName", report, "report");
        EventField eventField = eventField(tr, stageName);
        EvaluateEvent evaluateEvent = Creator.create(EvaluateEvent.class);
        evaluateEvent.call(runtimeKey, eventField, report, useFullClassNames());
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
        if (tr.hasIntentEventTypes(stageName))
        {
            return true;
        }
        return hasConfig(tr) && config(tr).indexes(stageName).hasIntentEventTypes();
    }
}
