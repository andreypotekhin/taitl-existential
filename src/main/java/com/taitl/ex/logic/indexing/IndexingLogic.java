package com.taitl.ex.logic.indexing;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.ex.logic.indexing.data.*;
import com.taitl.existential.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

public class IndexingLogic implements Closeable
{
    protected Existential ex;
    protected ExistentialConfigs ec;
    protected ExistentialEvents ev;

    public IndexingLogic(ExistentialEvents ev)
    {
        this.ev = ev;
        this.ex = ev.ex();
        this.ec = ex.configs();
    }

    public <T> void indexEvent(Event<T> event, T o, TypeKey<T> type, Tr tr)
    {
        IndexData indexData = tr.runtimeIndexes();
        boolean fullNames = ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        EventKey eventKey = fullNames ? EventKey.valueOfFull(event, type) : EventKey.valueOf(event, type);
        RuntimeKey<T> runtimeKey = RuntimeKey.valueOf(event, type, o, fullNames);
        indexData.encounteredUniqueEvents.add(runtimeKey);
        indexData.encounteredEventKeys.add(eventKey);
        indexData.encounteredTypeKeys.add(type);
    }

    public <T> void indexEvent(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        IndexData indexData = tr.runtimeIndexes();
        boolean fullNames = ex.get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
        EventKey eventKey = fullNames ? EventKey.valueOfFull(event, type) : EventKey.valueOf(event, type);
        // TODO: add clarification, perhaps a business rule, around choice of using event.t1
        RuntimeKey<T> runtimeKey = RuntimeKey.valueOf(event, type, event.t1, fullNames);
        indexData.encounteredUniqueEvents.add(runtimeKey);
        indexData.encounteredEventKeys.add(eventKey);
        indexData.encounteredTypeKeys.add(type);
    }

    public void close()
    {
    }
}
