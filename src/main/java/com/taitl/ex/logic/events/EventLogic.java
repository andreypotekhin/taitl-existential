package com.taitl.ex.logic.events;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.cross.caching.*;
import com.taitl.ex.logic.events.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

public class EventLogic implements Closeable
{
    protected Existential ex;
    protected ExistentialEvents ev;
    protected ReceiveEvent receiveEvent;
    protected TypeKeyCache typeKeyCache;

    public EventLogic(ExistentialEvents ev)
    {
        this.ev = ev;
        this.ex = ev.ex();
        this.receiveEvent = Creator.create(ReceiveEvent.class, new Class[] { EventLogic.class }, this);
        this.typeKeyCache = Creator.singleton(TypeKeyCache.class);
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", t, "t", type, "type", tr, "tr");
        receiveEvent.event(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", type, "type", tr, "tr");
        receiveEvent.event(event, type, tr);
    }

    public void close()
    {
    }

    public ExistentialEvents ev()
    {
        return ev;
    }

    protected Tr tr(String tranID) throws ExistentialException
    {
        return ex.transactions().tr(tranID);
    }

    protected <T> TypeKey<T> typeKey(T t)
    {
        return typeKeyCache.get(t, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
    }
}
