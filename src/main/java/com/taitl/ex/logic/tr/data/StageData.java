package com.taitl.ex.logic.tr.data;

import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.tr.keys.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

public class StageData
{
    public final Set<EventType> intentEventTypes = new LinkedHashSet<>();
    public final Set<IntentHandlerKey> biIntentKeys = new LinkedHashSet<>();
    public final ListMap<IntentHandlerKey, EventHandler<?>> intentHandlers = new ListMap<>();
}
