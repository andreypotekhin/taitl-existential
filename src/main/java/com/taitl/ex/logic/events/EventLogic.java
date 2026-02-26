package com.taitl.ex.logic.events;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.cross.caching.*;
import com.taitl.ex.logic.events.actions.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
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
        this.typeKeyCache = new TypeKeyCache();
    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(type, "type", tranID, "tranID");
        check(t0 != null || t1 != null, "One of t0 or t1 should not be null.");
        boolean haveNull = t0 == null || t1 == null;
        BiEvent<T> event = haveNull ? new Transit<>(t0, t1) : new Mutate<>(t0, t1);
        receiveEvent.bievent(event, type, tr(tranID));
    }

    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        // TODO: Verify the use of Change here
        receiveEvent.event(new Change<T>(t), t, type, tr(tranID));
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", tranID, "tranID");
        receiveEvent.bievent(new Mutate<T>(t0, t1), typeKey(t1), tr(tranID));
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        // TODO: Verify the use of Change here
        receiveEvent.event(new Change<T>(t), t, typeKey(t), tr(tranID));
    }

    public void close()
    {
    }

    public ExistentialEvents ev()
    {
        return ev;
    }

    Tr tr(String tranID) throws ExistentialException
    {
        return ex.transactions().tr(tranID);
    }

    protected <T> TypeKey<T> typeKey(T t)
    {
        return typeKeyCache.get(t, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
    }
}
