package com.taitl.ex.logic.indexing;

import java.io.*;
import com.taitl.ex.common.annotations.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

public class IndexingLogic implements Closeable
{
    @Up
    protected ExistentialEvents ev;

    @Up
    protected Existential ex;

    @Up
    protected ExistentialConfigs ec;

    public IndexingLogic(ExistentialEvents ev)
    {
        this.ev = ev;
        this.ex = ev.ex();
        this.ec = ex.configs();
    }

    public <T> void indexEvent(Event<T> event, T entity, TypeKey<T> type, Tr tr)
    {
        indexEvent(RuntimeKey.valueOf(event, type, entity, ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES)), tr);
    }

    public <T> void indexEvent(RuntimeKey<T> key, Tr tr)
    {
        if (tr.indexMaintenance() != null)
        {
            tr.indexMaintenance().accept(key);
        }
        IndexData data = tr.runtimeIndexes();
        data.encounteredUniqueEvents.add(key);
        data.encounteredEventKeys.add(key.key());
        data.encounteredTypeKeys.add(key.typeKey());
    }

    public <T> void indexEvent(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        indexEvent(event, event.t1 != null ? event.t1 : event.t0, type, tr);
    }

    public void close()
    {
    }
}
