package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
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
    protected final EvaluationLogic el;
    protected final EventSplitter eventSplitter;
    protected final ToSplitKeys toSplitKeys;
    protected final EvaluateSplitKeys evaluateSplitKeys;

    public EvaluateIntents(EvaluationLogic el)
    {
        sane(el, "el");
        this.el = el;
        this.eventSplitter = Creator.singleton(EventSplitter.class);
        this.toSplitKeys = new ToSplitKeys();
        this.evaluateSplitKeys = new EvaluateSplitKeys();
    }

    public <T> void call(RuntimeKey<T> runtimeKey, Tr tr, StageName stageName) throws ExistentialException
    {
        sane(tr, "tr", runtimeKey, "runtimeKey", stageName, "stageName");
        ConfigurationIndexes indexes = el.config(tr).indexes(stageName);
        if (!indexes.hasIntents() && !tr.hasIntents(stageName))
        {
            return;
        }

        Map<EventType, List<RuntimeKey<T>>> grouped = splitAndGroupByEventType(runtimeKey);
        iterateSplitKeys(grouped, indexes, tr, stageName);
    }

    public <T> Map<EventType, List<RuntimeKey<T>>> splitAndGroupByEventType(RuntimeKey<T> runtimeKey)
    {
        return toSplitKeys.call(eventSplitter.split(runtimeKey, el.useFullClassNames()));
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
