package com.taitl.ex.logic.events.actions;

import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class ReceiveEvent
{
    protected EventLogic el;
    protected IndexingLogic indexingLogic;

    public ReceiveEvent(EventLogic el)
    {
        this.el = el;
        this.indexingLogic = el.ev().indexingLogic;
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", t, "t", type, "type", tr, "tr");
        // TODO: Trigger processing of immediate event handlers
        // ...
        // Add event to indexes for late-stage processing
        indexingLogic.indexEvent(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", type, "type", tr, "tr");
        // TODO: implement event processing
        // ...
        indexingLogic.indexEvent(event, type, tr);
    }
}
