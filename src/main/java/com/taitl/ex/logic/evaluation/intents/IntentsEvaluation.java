package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.evaluation.intents.actions.*;
import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Orchestrates immediate evaluation of configured intents for a runtime event.
 */
public class IntentsEvaluation
{
    protected EvaluationLogic el;
    protected EventSplitter eventSplitter;
    protected ResolveIntentHandlers resolveIntentHandlers;
    protected EvaluateIntentHandlers evaluateIntentHandlers;

    public IntentsEvaluation(EvaluationLogic el)
    {
        sane(el, "el");
        this.el = el;
        this.eventSplitter = Creator.singleton(EventSplitter.class);
        this.resolveIntentHandlers = new ResolveIntentHandlers();
        this.evaluateIntentHandlers = new EvaluateIntentHandlers();
    }

    public <T> void call(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(tr, "tr", runtimeKey, "runtimeKey");
        ConfigurationIndexes indexes = el.config(tr).indexes();
        if (!indexes.hasIntentEventTypes() && !tr.hasIntentEventTypes())
        {
            return;
        }

        Map<String, List<RuntimeKey<T>>> grouped = groupByEventType(eventSplitter.split(runtimeKey));

        for (Map.Entry<String, List<RuntimeKey<T>>> entry : grouped.entrySet())
        {
            String eventTypeName = entry.getKey();
            List<RuntimeKey<T>> runtimeKeys = entry.getValue();

            if (!resolveIntentHandlers.eventTypeIsGuarded(eventTypeName, indexes, tr))
            {
                continue;
            }

            List<EventKey<T>> eventKeys = new ArrayList<>(runtimeKeys.size());
            for (RuntimeKey<T> runtimeKeyInGroup : runtimeKeys)
            {
                eventKeys.add(runtimeKeyInGroup.key());
            }
            MultiKey<T> multiKey = MultiKey.valueOf(eventKeys);

            List<EventHandler<?>> intents = resolveIntentHandlers.call(
                    multiKey, runtimeKeys, eventTypeName, indexes.intentField(), tr);
            if (intents.isEmpty())
            {
                throw missingIntent(runtimeKeys.get(0));
            }

            if (!evaluateIntentHandlers.allowed(intents, runtimeKeys.get(0).event()))
            {
                throw notAllowed(runtimeKeys.get(0));
            }
        }
    }

    protected <T> Map<String, List<RuntimeKey<T>>> groupByEventType(Set<RuntimeKey<T>> runtimeKeys)
    {
        sane(runtimeKeys, "runtimeKeys");
        Map<String, List<RuntimeKey<T>>> grouped = new LinkedHashMap<>();
        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            grouped.computeIfAbsent(eventTypeName(runtimeKey), ignored -> new ArrayList<>()).add(runtimeKey);
        }
        return grouped;
    }

    protected <T> String eventTypeName(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        String key = runtimeKey.key().toString();
        int left = key.indexOf('<');
        return left < 0 ? key : key.substring(0, left);
    }

    protected <T> IntentViolation missingIntent(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        String message = "No intent is configured for event '" + runtimeKey.key() + "'. "
                + "Define an intent for this event and type key, or remove event-type intent gating. "
                + "See /Troubleshooting.md#intent-violation";
        return new IntentViolation(message);
    }

    protected <T> IntentViolation notAllowed(RuntimeKey<T> runtimeKey)
    {
        sane(runtimeKey, "runtimeKey");
        String message = "Intent condition is not met for event '" + runtimeKey.key() + "'. "
                + "See /Troubleshooting.md#intent-violation";
        return new IntentViolation(message);
    }
}
