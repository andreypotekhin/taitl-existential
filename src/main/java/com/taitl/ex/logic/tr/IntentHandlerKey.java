package com.taitl.ex.logic.tr;

import com.taitl.existential.events.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class IntentHandlerKey
{
    public final EventType eventType;
    public final String typeKey;

    public IntentHandlerKey(EventType eventType, String typeKey)
    {
        sane(eventType, "eventType", typeKey, "typeKey");
        this.eventType = eventType;
        this.typeKey = typeKey;
    }

    public int hashCode()
    {
        return Objects.hash(eventType, typeKey);
    }

    public boolean equals(Object other)
    {
        if (other == this)
        {
            return true;
        }
        if (!(other instanceof IntentHandlerKey))
        {
            return false;
        }
        IntentHandlerKey key = (IntentHandlerKey) other;
        return eventType.equals(key.eventType) && typeKey.equals(key.typeKey);
    }
}
