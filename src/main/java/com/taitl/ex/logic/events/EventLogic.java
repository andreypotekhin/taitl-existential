package com.taitl.ex.logic.events;

import com.taitl.ex.common.annotations.*;
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
    @Up
    protected ExistentialEvents ev;

    @Up
    protected Existential ex;

    @Logic
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
        sane(event, "event", t, "t", type, "type", tr, "tr");
        receiveEvent.event(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        sane(event, "event", type, "type", tr, "tr");
        receiveEvent.event(event, type, tr);
    }

    public <T> void create(T t, TypeKey<T> type, Tr tr)
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Create<>(t), t, type, tr);
    }

    /**
     * Variant of create(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void create(T t, Tr tr)
    {
        sane(t, "t", tr, "tr");
        create(t, typeKey(t), tr);
    }

    public <T> void delete(T t, TypeKey<T> type, Tr tr)
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Delete<>(t), t, type, tr);
    }

    /**
     * Variant of delete(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void delete(T t, Tr tr)
    {
        sane(t, "t", tr, "tr");
        delete(t, typeKey(t), tr);
    }

    public <T> void update(T t, TypeKey<T> type, Tr tr)
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Update<>(t), t, type, tr);
    }

    /**
     * Variant of update(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void update(T t, Tr tr)
    {
        sane(t, "t", tr, "tr");
        update(t, typeKey(t), tr);
    }

    public <T> void transit(T t0, T t1, TypeKey<T> type, Tr tr)
    {
        sane(type, "type", tr, "tr");
        requireTransitEndpoints(t0, t1);
        event(new Transit<>(t0, t1), type, tr);
    }

    /**
     * Variant of transit(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void transit(T t0, T t1, Tr tr)
    {
        sane(tr, "tr");
        requireTransitEndpoints(t0, t1);
        transit(t0, t1, typeKey(t1), tr);
    }

    public <T> void port(T t0, T t1, TypeKey<T> type, Tr tr)
    {
        sane(type, "type", tr, "tr");
        check(t0 != null || t1 != null,
                "Port requires at least one non-null endpoint. See /Troubleshooting.md#event-endpoints");
        boolean haveNull = t0 == null || t1 == null;
        BiEvent<T> event = haveNull ? new Port<>(t0, t1) : new Transit<>(t0, t1);
        event(event, type, tr);
    }

    /**
     * Variant of port(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void port(T t0, T t1, Tr tr)
    {
        sane(tr, "tr");
        check(t0 != null || t1 != null,
                "Port requires at least one non-null endpoint. See /Troubleshooting.md#event-endpoints");
        T entity = (t1 != null) ? t1 : t0;
        port(t0, t1, typeKey(entity), tr);
    }

    protected void requireTransitEndpoints(Object before, Object after)
    {
        check(before != null && after != null,
                "Transit requires non-null before and after values; use port() for creation or deletion."
                        + " See /Troubleshooting.md#event-endpoints");
    }

    public <T> void read(T entity, TypeKey<T> type, Tr tr)
    {
        sane(entity, "entity", type, "type", tr, "tr");
        event(new Read<>(entity), entity, type, tr);
    }

    public <T> void read(T entity, Tr tr)
    {
        sane(entity, "entity", tr, "tr");
        read(entity, typeKey(entity), tr);
    }

    public <T> void write(T entity, TypeKey<T> type, Tr tr)
    {
        sane(entity, "entity", type, "type", tr, "tr");
        event(new Write<>(entity), entity, type, tr);
    }

    public <T> void write(T entity, Tr tr)
    {
        sane(entity, "entity", tr, "tr");
        write(entity, typeKey(entity), tr);
    }

    public void close()
    {
    }

    public ExistentialEvents ev()
    {
        return ev;
    }

    protected Tr tr(String tranID)
    {
        return ex.transactions().tr(tranID);
    }

    protected <T> TypeKey<T> typeKey(T t)
    {
        return typeKeyCache.get(t, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
    }
}
