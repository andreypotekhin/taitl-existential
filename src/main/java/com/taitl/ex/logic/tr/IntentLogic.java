package com.taitl.ex.logic.tr;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.concrete.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class IntentLogic
{
    @Up
    protected final ConcreteTr tr;

    public IntentLogic(ConcreteTr tr)
    {
        sane(tr, "tr");
        this.tr = tr;
    }

    public boolean hasIntents()
    {
        for (StageName stageName : StageName.values())
        {
            if (hasIntents(stageName))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasIntentEventType(EventType eventType)
    {
        for (StageName stageName : StageName.values())
        {
            if (hasIntentEventType(stageName, eventType))
            {
                return true;
            }
        }
        return false;
    }

    public boolean hasIntents(StageName stageName)
    {
        sane(stageName, "stageName");
        StageData data = data(stageName);
        return data != null && !data.intentEventTypes.isEmpty();
    }

    public boolean hasIntentEventType(StageName stageName, EventType eventType)
    {
        sane(stageName, "stageName", eventType, "eventType");
        StageData data = data(stageName);
        return data != null && data.intentEventTypes.contains(eventType);
    }

    public boolean hasBiIntentHandler(StageName stageName, EventType eventType, String typeKey)
    {
        sane(stageName, "stageName", eventType, "eventType", typeKey, "typeKey");
        StageData data = data(stageName);
        return data != null && data.biIntentKeys.contains(intentKey(eventType, typeKey));
    }

    public List<EventHandler<?>> intentHandlers(EventType eventType, String typeKey)
    {
        return intentHandlers(StageName.IMMEDIATE, eventType, typeKey);
    }

    public List<EventHandler<?>> intentHandlers(StageName stageName, EventType eventType, String typeKey)
    {
        sane(stageName, "stageName", eventType, "eventType", typeKey, "typeKey");
        StageData data = data(stageName);
        return data != null ? data.intentHandlers.get(intentKey(eventType, typeKey)) : null;
    }

    public void indexIntent(StageName stageName, Intent<?> intent)
    {
        sane(stageName, "stageName", intent, "intent");
        StageData data = data(stageName);
        sane(data, "data");
        String typeKey = intent.typeKey().toString();
        for (Ev<?> ev : intent.list())
        {
            if (!(ev instanceof EventHandler<?>))
            {
                continue;
            }
            EventHandler<?> handler = (EventHandler<?>) ev;
            EventType eventType = handler.eventType();
            data.intentEventTypes.add(eventType);
            data.intentHandlers.add(intentKey(eventType, typeKey), handler);
            if (eventType.biEvent())
            {
                data.biIntentKeys.add(intentKey(eventType, typeKey));
            }
        }
    }

    public void indexContextIntents(Context context)
    {
        sane(context, "context");
        for (StageName stageName : StageName.values())
        {
            for (Evs<?> evs : context.stage().at(stageName))
            {
                if (!(evs instanceof Intent<?>))
                {
                    continue;
                }
                indexIntent(stageName, (Intent<?>) evs);
            }
        }
    }

    public boolean matchesType(TypeKey<?> configuredType, Transaction transaction)
    {
        sane(configuredType, "configuredType", transaction, "transaction");
        Class<?> type = transaction.getClass();
        while (type != null && Transaction.class.isAssignableFrom(type))
        {
            TypeKey<?> shortName = TypeKey.valueOf(type, false);
            TypeKey<?> fullName = TypeKey.valueOf(type, true);
            if (configuredType.equals(shortName) || configuredType.equals(fullName))
            {
                return true;
            }
            type = type.getSuperclass();
        }
        return false;
    }

    public IntentHandlerKey intentKey(EventType eventType, String typeKey)
    {
        sane(eventType, "eventType", typeKey, "typeKey");
        return new IntentHandlerKey(eventType, typeKey);
    }

    protected StageData data(StageName stageName)
    {
        sane(stageName, "stageName");
        return tr.stageData().get(stageName);
    }
}
