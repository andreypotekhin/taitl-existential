package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.ex.common.annotations.*;
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
    protected final ToIntentHandlers toIntentHandlers;

    @Logic
    protected final IterateIntents iterateIntents;

    @Logic
    protected final CheckEventType checkEventType;

    public EvaluateSplitKeys()
    {
        this(Creator.create(ToIntentHandlers.class), Creator.create(IterateIntents.class));
    }

    protected EvaluateSplitKeys(
            ToIntentHandlers toIntentHandlers,
            IterateIntents iterateIntents)
    {
        sane(toIntentHandlers, "resolveIntentHandlers", iterateIntents, "evaluateIntentHandlers");
        this.toIntentHandlers = toIntentHandlers;
        this.iterateIntents = iterateIntents;
        this.checkEventType = Creator.create(CheckEventType.class);
    }

    public <T> void call(
            EventType eventType,
            List<RuntimeKey<T>> runtimeKeys,
            ConfigurationIndexes indexes,
            Tr tr,
            StageName stageName) throws ExistentialException
    {
        sane(eventType, "eventType", runtimeKeys, "runtimeKeys", indexes, "indexes", tr, "tr", stageName,
                "stageName");
        if (!checkEventType.eventTypeIsGuarded(eventType, indexes, tr, stageName))
        {
            return;
        }

        RuntimeKey<T> primaryRuntimeKey = primaryRuntimeKey(runtimeKeys);
        List<EventHandler<?>> intents =
                toIntentHandlers.call(toMultiKey(runtimeKeys), runtimeKeys, eventType, indexes.intentField(),
                        tr, stageName);
        if (intents.isEmpty())
        {
            throw missingIntent(primaryRuntimeKey);
        }
        if (!iterateIntents.allowed(intents, primaryRuntimeKey.event()))
        {
            throw notAllowed(primaryRuntimeKey);
        }
    }

    protected <T> RuntimeKey<T> primaryRuntimeKey(List<RuntimeKey<T>> runtimeKeys)
    {
        sane(runtimeKeys, "runtimeKeys");
        if (runtimeKeys.isEmpty())
        {
            throw new IllegalStateException("Runtime key group should not be empty");
        }
        return runtimeKeys.get(0);
    }

    protected <T> MultiKey<T> toMultiKey(List<RuntimeKey<T>> runtimeKeys)
    {
        sane(runtimeKeys, "runtimeKeys");
        List<EventKey<T>> eventKeys = new ArrayList<>(runtimeKeys.size());
        for (RuntimeKey<T> runtimeKey : runtimeKeys)
        {
            eventKeys.add(runtimeKey.key());
        }
        return MultiKey.valueOf(eventKeys);
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
