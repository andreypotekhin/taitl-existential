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
        this.typeKeyCache = Creator.singleton(TypeKeyCache.class);
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "event", t, "t", type, "type", tr, "tr");
        receiveEvent.event(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "event", type, "type", tr, "tr");
        receiveEvent.event(event, type, tr);
    }

    public <T> void create(T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Create<>(t), t, type, tr);
    }

    /**
     * Variant of create(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void create(T t, Tr tr) throws ExistentialException
    {
        sane(t, "t", tr, "tr");
        create(t, typeKey(t), tr);
    }

    public <T> void delete(T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Delete<>(t), t, type, tr);
    }

    /**
     * Variant of delete(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void delete(T t, Tr tr) throws ExistentialException
    {
        sane(t, "t", tr, "tr");
        delete(t, typeKey(t), tr);
    }

    public <T> void update(T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(t, "t", type, "type", tr, "tr");
        event(new Update<>(t), t, type, tr);
    }

    /**
     * Variant of update(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void update(T t, Tr tr) throws ExistentialException
    {
        sane(t, "t", tr, "tr");
        update(t, typeKey(t), tr);
    }

    public <T> void mutate(T t0, T t1, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", type, "type", tr, "tr");
        event(new Mutate<>(t0, t1), type, tr);
    }

    /**
     * Variant of mutate(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void mutate(T t0, T t1, Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        check(t0 != null && t1 != null, "Both t0 and t1 should not be null.");
        mutate(t0, t1, typeKey(t1), tr);
    }

    public <T> void port(T t0, T t1, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(type, "type", tr, "tr");
        check(t0 != null || t1 != null, "One of t0 or t1 should not be null.");
        boolean haveNull = t0 == null || t1 == null;
        BiEvent<T> event = haveNull ? new Port<>(t0, t1) : new Mutate<>(t0, t1);
        event(event, type, tr);
    }

    /**
     * Variant of port(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void port(T t0, T t1, Tr tr) throws ExistentialException
    {
        sane(tr, "tr");
        check(t0 != null || t1 != null, "One of t0 or t1 should not be null.");
        T entity = (t1 != null) ? t1 : t0;
        port(t0, t1, typeKey(entity), tr);
    }

    public <T> void read(T entity, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(entity, "entity", type, "type", tr, "tr");
        event(new Read<>(entity), entity, type, tr);
    }

    public <T> void read(T entity, Tr tr) throws ExistentialException
    {
        sane(entity, "entity", tr, "tr");
        read(entity, typeKey(entity), tr);
    }

    public <T> void write(T entity, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(entity, "entity", type, "type", tr, "tr");
        event(new Write<>(entity), entity, type, tr);
    }

    public <T> void write(T entity, Tr tr) throws ExistentialException
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

    protected Tr tr(String tranID) throws ExistentialException
    {
        return ex.transactions().tr(tranID);
    }

    protected <T> TypeKey<T> typeKey(T t)
    {
        return typeKeyCache.get(t, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES));
    }
}
