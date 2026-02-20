package com.taitl.ex.logic.events.actions;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

public class ReceiveEvent
{
    protected EventLogic el;
    protected SplitEvent splitEvent = Creator.singleton(SplitEvent.class);
    protected IndexingLogic indexingLogic;

    public ReceiveEvent(EventLogic el)
    {
        this.el = el;
        this.indexingLogic = Creator.create(IndexingLogic.class,
                new Class[] { ExistentialEvents.class },
                el.ev());
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr)
    {
        // TODO: implement event processing
        // Split event into elementary events using EventSplitter
        // Transit<House> -> On<House>, Mutate<House>, Transit<House>
        // Then, depending on mutation type: OnCreate<House>, OnUpdate<House>, OnMutate<House>,
        // OnDelete<House>, CU<House>, CUD<House>, UD<House>
        // Trigger processing of immediate event handlers
        // ...
        // Add each event to indexes for late-stage processing
        indexingLogic.indexEvent(event, t, type, tr);
    }

    public <T> void bievent(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        // TODO: implement event processing
        // ...
        indexingLogic.indexEvent(event, type, tr);
    }
}
