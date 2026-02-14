package com.taitl.ex.logic.events.actions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

public class ReceiveEvent
{
    protected EventLogic el;
    protected SplitEvent splitEvent = Creator.singleton(SplitEvent.class);
    protected IndexingLogic indexingLogic = Creator.singleton(IndexingLogic.class);

    public ReceiveEvent(EventLogic el)
    {
        this.el = el;
    }

    public <T> void bievent(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        // TODO: implement event receiving logic
        // Split event into multiple events using EventSplitter
        // Transit<House> -> On<House>, Mutate<House>, Transit<House>
        // Depending on mutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>
        // Trigger processing of immediate event handlers
        // Add event to event index for late-phase processing
        indexingLogic.indexEvent(event, type, tr);
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr)
    {
        // TODO: implement event receiving logic
        // ...
        indexingLogic.indexEvent(event, t, type, tr);
    }
}
