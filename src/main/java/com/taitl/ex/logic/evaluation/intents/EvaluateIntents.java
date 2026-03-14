package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.evaluation.events.split_events.rules.*;
import com.taitl.ex.logic.evaluation.intents.actions.*;
import com.taitl.ex.logic.evaluation.intents.maps.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Evaluates the configured intents applicable for a runtime event.
 */
public class EvaluateIntents
{
    @Up
    protected final EvaluationLogic el;
    protected final EventSplitter eventSplitter;
    protected final ToSplitKeys toSplitKeys;
    protected final EvaluateSplitKeys evaluateSplitKeys;
    protected final RequireMemoForBiRules requireMemoForBiRules;

    public EvaluateIntents(EvaluationLogic el)
    {
        sane(el, "el");
        this.el = el;
        this.eventSplitter = Creator.singleton(EventSplitter.class);
        this.toSplitKeys = new ToSplitKeys();
        this.evaluateSplitKeys = new EvaluateSplitKeys();
        this.requireMemoForBiRules = Creator.create(RequireMemoForBiRules.class);
    }

    public <T> void call(RuntimeKey<T> runtimeKey, Tr tr, StageName stageName) throws ExistentialException
    {
        sane(tr, "tr", runtimeKey, "runtimeKey", stageName, "stageName");
        ConfigurationIndexes indexes = el.config(tr).indexes(tr.op, stageName);
        if (!indexes.hasIntents() && !tr.hasIntents(stageName))
        {
            return;
        }

        requireMemoForBiRules.forIntents(runtimeKey, indexes, tr, stageName);
        Map<EventType, List<RuntimeKey<T>>> grouped = splitAndGroupByEventType(runtimeKey, tr);
        iterateSplitKeys(grouped, indexes, tr, stageName);
    }

    public <T> Map<EventType, List<RuntimeKey<T>>> splitAndGroupByEventType(RuntimeKey<T> runtimeKey, Tr tr)
            throws ExistentialException
    {
        return toSplitKeys
                .call(eventSplitter.split(runtimeKey, el.useFullClassNames(), el.shouldSplitElementary(), tr));
    }

    public <T> Map<EventType, List<RuntimeKey<T>>> splitAndGroupByEventType(RuntimeKey<T> runtimeKey)
    {
        try
        {
            return toSplitKeys
                    .call(eventSplitter.split(runtimeKey, el.useFullClassNames(), el.shouldSplitElementary(), null));
        }
        catch (ExistentialException ex)
        {
            throw new IllegalStateException("Split without transaction should not require memo resolution", ex);
        }
    }

    public <T> void iterateSplitKeys(
            Map<EventType, List<RuntimeKey<T>>> grouped,
            ConfigurationIndexes indexes,
            Tr tr,
            StageName stageName) throws ExistentialException
    {
        sane(grouped, "grouped", indexes, "indexes", tr, "tr", stageName, "stageName");
        for (Map.Entry<EventType, List<RuntimeKey<T>>> entry : grouped.entrySet())
        {
            evaluateSplitKeys.call(entry.getKey(), entry.getValue(), indexes, tr, stageName);
        }
    }
}
