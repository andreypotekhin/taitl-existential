package com.taitl.ex.core.existential;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.cross.caching.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.*;
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

public class ExistentialEvents implements Closeable
{
    protected Existential ex;
    public EventLogic eventLogic;
    public IndexingLogic indexingLogic;
    protected TypeKeyCache typeKeyCache;

    public ExistentialEvents(Existential ex)
    {
        this.ex = ex;
        this.eventLogic = Creator.create(EventLogic.class,
                new Class[] { ExistentialEvents.class }, this);
        this.indexingLogic = Creator.create(IndexingLogic.class,
                new Class[] { ExistentialEvents.class }, this);
        this.typeKeyCache = Creator.singleton(TypeKeyCache.class);
    }

    /* Core methods */

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", t, "t", type, "type", tr, "tr");
        eventLogic.event(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        sane(event, "type", type, "type", tr, "tr");
        eventLogic.event(event, type, tr);
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(event, "type", t, "t", type, "type", tranID, "tranID");
        event(event, t, type, tr(tranID));
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(event, "type", type, "type", tranID, "tranID");
        event(event, type, tr(tranID));
    }

    /* Convenience methods */

    public <T> void create(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        event(new Create<>(t), t, type, tr(tranID));
    }

    /**
     * Variant of create(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void create(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        event(new Create<>(t), t, typeKey(t), tranID);
    }

    public <T> void delete(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        event(new Delete<>(t), t, type, tr(tranID));
    }

    /**
     * Variant of delete(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void delete(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        event(new Delete<>(t), t, typeKey(t), tranID);
    }

    public <T> void modify(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        event(new Modify<>(t), t, type, tr(tranID));
    }

    /**
     * Variant of modify(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void modify(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        event(new Modify<>(t), t, typeKey(t), tranID);
    }

    public <T> void update(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        event(new Update<>(t), t, type, tr(tranID));
    }

    /**
     * Variant of update(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void update(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        event(new Update<>(t), t, typeKey(t), tranID);
    }

    public <T> void change(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        event(new Change<T>(t), t, type, tr(tranID));
    }

    /**
     * Variant of event(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Only suitable for non-generic types.
     */
    public <T> void change(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        event(new Change<T>(t), t, typeKey(t), tranID);
    }

    public <T> void mutate(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", type, "type", tranID, "tranID");
        event(new Mutate<>(t0, t1), type, tranID);
    }

    /**
     * Variant of mutate(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void mutate(T t0, T t1, String tranID) throws ExistentialException
    {
        mutate(t0, t1, typeKey(t1), tranID);
    }

    public <T> void transit(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(type, "type", tranID, "tranID");
        check(t0 != null || t1 != null, "One of t0 or t1 should not be null.");
        boolean haveNull = t0 == null || t1 == null;
        BiEvent<T> event = haveNull ? new Transit<>(t0, t1) : new Mutate<>(t0, t1);
        event(event, type, tr(tranID));
    }

    /**
     * Variant of transit(t0, t1) without type parameter,
     * only suitable for non-generic types.
     */
    public <T> void transit(T t0, T t1, String tranID) throws ExistentialException
    {
        transit(t0, t1, typeKey(t1), tranID);
    }

    public void close()
    {
        eventLogic.close();
    }

    /* Attributes */

    public Existential ex()
    {
        return ex;
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
