package com.taitl.ex.core.existential;

import com.taitl.ex.cross.caching.*;
import com.taitl.ex.logic.events.*;
import java.io.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExistentialAccess implements Closeable
{
    protected Existential ex;
    protected TypeKeyCache typeKeyCache;

    public ExistentialAccess(Existential ex)
    {
        this.ex = ex;
        this.typeKeyCache = com.taitl.ex.common.creator.Creator.singleton(TypeKeyCache.class);
    }

    public <T> void read(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(entity, "entity", type, "type", tranID, "tranID");
        eventLogic().event(new Read<>(entity), entity, type, tr(tranID));
    }

    public <T> void read(T entity, String tranID) throws ExistentialException
    {
        sane(entity, "entity", tranID, "tranID");
        read(entity, typeKey(entity), tranID);
    }

    public <T> void write(T entity, TypeKey<T> type, String tranID) throws ExistentialException
    {
        sane(entity, "entity", type, "type", tranID, "tranID");
        eventLogic().event(new Write<>(entity), entity, type, tr(tranID));
    }

    public <T> void write(T entity, String tranID) throws ExistentialException
    {
        sane(entity, "entity", tranID, "tranID");
        write(entity, typeKey(entity), tranID);
    }

    public void close()
    {
    }

    protected EventLogic eventLogic()
    {
        return ex.events().eventLogic;
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
