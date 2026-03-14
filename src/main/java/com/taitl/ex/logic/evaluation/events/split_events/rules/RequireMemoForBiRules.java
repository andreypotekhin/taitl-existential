package com.taitl.ex.logic.evaluation.events.split_events.rules;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.split_events.event_splitter.*;
import com.taitl.ex.logic.evaluation.intents.maps.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.combined_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class RequireMemoForBiRules
{
    @Logic
    protected final SplitTypeKey splitTypeKey;

    @Logic
    protected final ResolveMemoBiEvent resolveMemoBiEvent;

    @Logic
    protected final IntentTypeCandidates intentTypeCandidates;

    public RequireMemoForBiRules()
    {
        this.splitTypeKey = Creator.create(SplitTypeKey.class);
        this.resolveMemoBiEvent = Creator.create(ResolveMemoBiEvent.class);
        this.intentTypeCandidates = Creator.create(IntentTypeCandidates.class);
    }

    public <T> void forHandlers(RuntimeKey<T> runtimeKey, EventField eventField, boolean useFullEventNames, Tr tr)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField", tr, "tr");
        if (!resolveMemoBiEvent.memoSensitive(runtimeKey) || tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey()))
        {
            return;
        }
        for (TypeKey<T> typeKey : splitTypeKey.split(runtimeKey.typeKey()))
        {
            if (requiresHandlerMemo(typeKey, eventField, useFullEventNames, runtimeKey.event()))
            {
                resolveMemoBiEvent.forSplit(
                        newEvent(candidateBiEventClasses(runtimeKey.event())[0], runtimeKey.entity()),
                        runtimeKey, tr);
                return;
            }
        }
    }

    public <T> void forIntents(RuntimeKey<T> runtimeKey, ConfigurationIndexes indexes, Tr tr, StageName stageName)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", indexes, "indexes", tr, "tr", stageName, "stageName");
        if (!resolveMemoBiEvent.memoSensitive(runtimeKey) || tr.hasMemo(runtimeKey.entity(), runtimeKey.typeKey()))
        {
            return;
        }
        for (TypeKey<T> typeKey : splitTypeKey.split(runtimeKey.typeKey()))
        {
            List<String> candidates = intentTypeCandidates.call(typeKey, runtimeKey.entity());
            for (Class<?> eventClass : candidateBiEventClasses(runtimeKey.event()))
            {
                EventType eventType = EventType.valueOf(eventClass);
                for (String candidate : candidates)
                {
                    if (requiresIntentMemo(indexes, tr, stageName, eventType, candidate))
                    {
                        resolveMemoBiEvent.forSplit(newEvent(eventClass, runtimeKey.entity()), runtimeKey, tr);
                        return;
                    }
                }
            }
        }
    }

    protected <T> boolean requiresHandlerMemo(
            TypeKey<T> typeKey,
            EventField eventField,
            boolean useFullEventNames,
            Event<T> rootEvent)
    {
        sane(typeKey, "typeKey", eventField, "eventField", rootEvent, "rootEvent");
        for (Class<?> eventClass : candidateBiEventClasses(rootEvent))
        {
            EventKey<T> eventKey = useFullEventNames ? EventKey.valueOfFull(eventClass, typeKey)
                    : EventKey.valueOf(eventClass, typeKey);
            if (eventField.hasBiEventKey(eventKey))
            {
                return true;
            }
        }
        return false;
    }

    protected boolean requiresIntentMemo(
            ConfigurationIndexes indexes,
            Tr tr,
            StageName stageName,
            EventType eventType,
            String candidate)
    {
        sane(indexes, "indexes", tr, "tr", stageName, "stageName", eventType, "eventType", candidate, "candidate");
        EventKey<?> eventKey = EventKey.valueOf(eventType.eventClass(), candidate, false);
        return indexes.hasBiKey(eventKey) || tr.hasBiIntentHandler(stageName, eventType, candidate);
    }

    protected Class<?>[] candidateBiEventClasses(Event<?> event)
    {
        sane(event, "event");
        if (event instanceof Delete<?>)
        {
            return new Class[] { Port.class };
        }
        if (event instanceof Update<?>)
        {
            return new Class[] { Transit.class };
        }
        if (event instanceof CU<?> || event instanceof UD<?> || event instanceof CUD<?>)
        {
            return new Class[] { Transit.class, Port.class };
        }
        return new Class[] {};
    }

    @SuppressWarnings("unchecked")
    protected <T> Event<T> newEvent(Class<?> eventClass, T entity)
    {
        if (eventClass.equals(Transit.class))
        {
            return (Event<T>) new Transit<>(entity, entity);
        }
        return (Event<T>) new Port<>(entity, entity);
    }
}
