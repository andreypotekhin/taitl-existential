package com.taitl.ex.core.existential;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import java.io.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExistentialEvents implements Closeable
{
    protected Existential ex;
    public EventLogic eventLogic;
    public IndexingLogic indexingLogic;

    public ExistentialEvents(Existential ex)
    {
        this.ex = ex;
        this.eventLogic = Creator.create(EventLogic.class,
                new Class[] { ExistentialEvents.class },
                this);
        this.indexingLogic = Creator.create(IndexingLogic.class,
                new Class[] { ExistentialEvents.class },
                this);

    }

    public <T> void event(T t0, T t1, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", type, "type", tranID, "tranID");
        eventLogic.event(t0, t1, type, tranID);
    }

    public <T> void event(T t, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(t, "t", type, "type", tranID, "tranID");
        eventLogic.event(t, type, tranID);
    }

    /**
     * Variant of send(t0, t1) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t0, T t1, String tranID) throws ExistentialException
    {
        sane(t0, "t0", t1, "t1", tranID, "tranID");
        eventLogic.event(t0, t1, tranID);
    }

    /**
     * Variant of send(t) without type parameter, when entity type may be
     * deducted at run time - that is, if entity class is not generic.
     * Do not use for generic classes, such as Document<T> and the like.
     */
    public <T> void event(T t, String tranID) throws ExistentialException
    {
        sane(t, "t", tranID, "tranID");
        eventLogic.event(t, tranID);
    }

    public void close()
    {
        eventLogic.close();
    }

    public Existential ex()
    {
        return ex;
    }
}
