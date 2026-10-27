package com.taitl.ex.logic.evaluation;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.execution.rules.*;
import com.taitl.ex.logic.evaluation.events.*;
import com.taitl.ex.logic.evaluation.intents.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.ex.logic.transactions.*;
import com.taitl.ex.logic.types.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluationLogic implements Closeable
{
    @Up
    protected TransactionLogic tl;

    @Logic
    protected EvaluateEvents evaluateEvents;

    @Logic
    protected EvaluateIntents evaluateIntents;

    @Logic
    protected RequiresStageTraversal requiresStageTraversal = Creator.create(RequiresStageTraversal.class);

    @Logic
    protected TypeMatchingLogic typeMatchingLogic = Creator.create(TypeMatchingLogic.class);

    public EvaluationLogic(TransactionLogic tl)
    {
        this.tl = tl;
        this.evaluateEvents = Creator.create(EvaluateEvents.class);
        this.evaluateIntents = Creator.create(EvaluateIntents.class, new Class[] { EvaluationLogic.class }, this);
    }

    public <T> void evaluateBegin(RuntimeKey<T> runtimeKey, Tr tr)
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateEvents(runtimeKey, tr, StageName.BEGIN, report);
        throwIfStageFailed(StageName.BEGIN, report);
    }

    public <T> void evaluateImmediate(RuntimeKey<T> runtimeKey, Tr tr)
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        ValidationReport report = Creator.create(ValidationReport.class);
        evaluateEvents(runtimeKey, tr, StageName.IMMEDIATE, report);
        throwIfStageFailed(StageName.IMMEDIATE, report);
    }

    public void evaluateCommit(Tr tr)
    {
        evaluateEncountered(tr, StageName.COMMIT);
    }

    public void evaluateCheckpoint(Tr tr)
    {
        evaluateEncountered(tr, StageName.CHECKPOINT);
    }

    public void evaluateRollback(Tr tr)
    {
        sane(tr, "tr");
        if (!requiresTraversal(tr, StageName.ROLLBACK))
        {
            return;
        }
        ValidationReport report = Creator.create(ValidationReport.class);
        Throwable failure = null;
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            try
            {
                if (hasIntents(tr, StageName.ROLLBACK))
                {
                    evaluateIntents(runtimeKey, tr, StageName.ROLLBACK);
                }
            }
            catch (Throwable caught)
            {
                failure = append(failure, caught);
            }
            try
            {
                evaluateRollbackEvents(runtimeKey, tr, report);
            }
            catch (Throwable caught)
            {
                failure = append(failure, caught);
            }
        }
        try
        {
            throwIfStageFailed(StageName.ROLLBACK, report);
        }
        catch (Throwable caught)
        {
            failure = append(failure, caught);
        }
        rethrow(failure);
    }

    public void evaluateValidation(Tr tr, ValidationReport report)
    {
        sane(tr, "tr", report, "report");
        if (!requiresTraversal(tr, StageName.VALIDATION))
        {
            return;
        }
        boolean stopAfterViolation = hasSelectedEffects(tr);
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            if (hasIntents(tr, StageName.VALIDATION))
            {
                evaluateIntents(runtimeKey, tr, StageName.VALIDATION);
            }
            evaluateEvents(runtimeKey, tr, StageName.VALIDATION, report, stopAfterViolation);
            if (stopAfterViolation && !report.isEmpty())
            {
                return;
            }
        }
    }

    protected boolean hasSelectedEffects(Tr tr)
    {
        if (!eventField(tr, StageName.VALIDATION).hasEffects())
        {
            return false;
        }
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            if (hasSelectedEffects(runtimeKey, tr))
            {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected boolean hasSelectedEffects(RuntimeKey runtimeKey, Tr tr)
    {
        EventField eventField = eventField(tr, StageName.VALIDATION);
        return evaluateEvents.hasEffects(runtimeKey, eventField,
                useFullClassNames(), shouldSplitElementary(), tr);
    }

    protected <T> void evaluateEvents(
            RuntimeKey<T> runtimeKey,
            Tr tr,
            StageName stageName,
            ValidationReport report)
    {
        evaluateEvents(runtimeKey, tr, stageName, report, false);
    }

    protected <T> void evaluateEvents(
            RuntimeKey<T> runtimeKey,
            Tr tr,
            StageName stageName,
            ValidationReport report,
            boolean stopAfterViolation)
    {
        sane(runtimeKey, "runtimeKey", tr, "tr", stageName, "stageName", report, "report");
        warnUnresolvedGenerics(runtimeKey, tr);
        EventField eventField = eventField(tr, stageName);
        evaluateEvents.call(runtimeKey, tr, eventField, report, useFullClassNames(), shouldSplitElementary(),
                stopAfterViolation);
    }

    protected void evaluateEncountered(Tr tr, StageName stageName)
    {
        sane(tr, "tr", stageName, "stageName");
        if (!requiresTraversal(tr, stageName))
        {
            return;
        }
        ValidationReport report = Creator.create(ValidationReport.class);
        for (RuntimeKey<?> runtimeKey : tr.runtimeIndexes().encounteredUniqueEvents)
        {
            if (hasIntents(tr, stageName))
            {
                evaluateIntents(runtimeKey, tr, stageName);
            }
            evaluateEvents(runtimeKey, tr, stageName, report);
        }
        throwIfStageFailed(stageName, report);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void evaluateRollbackEvents(RuntimeKey runtimeKey, Tr tr, ValidationReport report)
    {
        EventField field = eventField(tr, StageName.ROLLBACK);
        warnUnresolvedGenerics(runtimeKey, tr);
        evaluateEvents.callAll(runtimeKey, tr, field, report, useFullClassNames(), shouldSplitElementary());
    }

    protected boolean requiresTraversal(Tr tr, StageName stage)
    {
        return requiresStageTraversal.in(config(tr).indexes(tr.op, stage), tr, stage);
    }

    public void warnUnresolvedGenerics(RuntimeKey<?> runtimeKey, Tr tr)
    {
        Object entity = runtimeKey.entity();
        if (entity != null)
        {
            typeMatchingLogic.warnUnresolvedGenerics(tr.op, runtimeKey.typeKey(), entity.getClass(),
                    warnUnresolvedGenerics());
        }
    }

    protected Throwable append(Throwable failure, Throwable next)
    {
        if (failure == null)
        {
            return next;
        }
        if (failure != next)
        {
            failure.addSuppressed(next);
        }
        return failure;
    }

    protected void rethrow(Throwable failure)
    {
        if (failure instanceof ExistentialException)
        {
            throw (ExistentialException) failure;
        }
        if (failure instanceof RuntimeException)
        {
            throw (RuntimeException) failure;
        }
        if (failure instanceof Error)
        {
            throw (Error) failure;
        }
        if (failure != null)
        {
            throw new ExistentialException("Rollback evaluation failed", failure);
        }
    }

    public <T> void evaluateIntents(RuntimeKey<T> runtimeKey, Tr tr, StageName stageName)
    {
        sane(stageName, "stageName");
        sane(tr, "tr", runtimeKey, "runtimeKey");
        tr.callbackStarted();
        try
        {
            evaluateIntents.call(runtimeKey, tr, stageName);
        }
        finally
        {
            tr.callbackFinished();
        }
    }

    public void close()
    {
        typeMatchingLogic.close();
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
        EventField base = stageName == StageName.VALIDATION
                ? eventField(tr)
                : config(tr).indexes(tr.op, stageName).eventField();
        return tr.eventField(base, stageName);
    }

    public boolean useFullClassNames()
    {
        return tl.ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }

    public boolean shouldSplitElementary()
    {
        return !tl.ex().get(Flags.EVENT_SPLIT_DISABLE_ELEMENTARY_TO_COMPOUND);
    }

    public boolean warnUnresolvedGenerics()
    {
        return tl.ex().get(Flags.TYPE_MATCHING_WARN_UNRESOLVED_GENERICS);
    }

    protected void throwIfStageFailed(StageName stageName, ValidationReport report)
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
