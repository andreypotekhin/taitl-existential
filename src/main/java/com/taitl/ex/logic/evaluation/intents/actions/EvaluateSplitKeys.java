package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.diagnostics.*;
import com.taitl.ex.logic.evaluation.events.split_events.maps.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.ex.logic.evaluation.intents.maps.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateSplitKeys
{
    @Logic
    protected CheckEventType checkEventType = Creator.create(CheckEventType.class);

    @Logic
    protected DiagnosticLogic diagnostics = Creator.singleton(DiagnosticLogic.class);

    @Logic
    protected EvaluateIntent evaluateIntent = Creator.create(EvaluateIntent.class);

    @Logic
    protected IntentTypeCandidates intentTypeCandidates = Creator.create(IntentTypeCandidates.class);

    @Logic
    protected ToMemo toMemo = Creator.create(ToMemo.class);

    public <T> void call(
            Map<EventType, List<RuntimeKey<T>>> grouped,
            ConfigurationIndexes indexes,
            Tr tr,
            StageName stageName)
    {
        RuntimeKey<T> original = grouped.isEmpty() ? null : grouped.values().iterator().next().get(0);
        call(grouped, indexes, tr, stageName, original);
    }

    public <T> void call(Map<EventType, List<RuntimeKey<T>>> grouped, ConfigurationIndexes indexes, Tr tr,
            StageName stageName, RuntimeKey<T> original)
    {
        sane(grouped, "grouped", indexes, "indexes", tr, "tr", stageName, "stageName");
        EventField field = tr.intentField(indexes.intentField(), stageName);
        List<EventKey<T>> matchedKeys = new ArrayList<>();
        for (Map.Entry<EventType, List<RuntimeKey<T>>> entry : grouped.entrySet())
        {
            EventType eventType = entry.getKey();
            if (!checkEventType.eventTypeIsGuarded(eventType, indexes, tr, stageName))
            {
                continue;
            }
            MultiKey<T> keys = toMultiKey(entry.getValue(), eventType);
            if (field.selection(keys).handlers().isEmpty())
            {
                throw diagnostics.missing(tr.op, original, stageName, tr.evaluationBoundary());
            }
            matchedKeys.addAll(keys.eventKeys());
        }
        if (matchedKeys.isEmpty())
        {
            return;
        }
        RuleSelection selected = field.selection(MultiKey.valueOf(matchedKeys));
        for (int i = 0; i < selected.handlers().size(); i++)
        {
            RuleDeclaration declaration = selected.declaration(i);
            EventHandler<?> handler = (EventHandler<?>) selected.handlers().get(i);
            Event<?> event = toMemo.forExecution(original, handler instanceof BiEventHandler<?>, tr);
            try
            {
                evaluateIntent.require(handler, event);
            }
            catch (RuntimeException failure)
            {
                throw diagnostics.failure(failure, tr.op, declaration == null ? null : declaration.origin,
                        original, stageName, tr.evaluationBoundary(), true, true);
            }
        }
    }

    protected <T> MultiKey<T> toMultiKey(List<RuntimeKey<T>> runtimeKeys)
    {
        return toMultiKey(runtimeKeys, null);
    }

    protected <T> MultiKey<T> toMultiKey(List<RuntimeKey<T>> runtimeKeys, EventType eventType)
    {
        sane(runtimeKeys, "runtimeKeys");
        List<EventKey<T>> eventKeys = new ArrayList<>(runtimeKeys.size());
        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            for (String candidate : intentTypeCandidates.call(runtimeKey))
            {
                eventKeys.add(eventType != null ? new EventKey<>(eventType.eventClass(), candidate) : runtimeKey.key());
            }
        }
        return MultiKey.valueOf(eventKeys);
    }

}
