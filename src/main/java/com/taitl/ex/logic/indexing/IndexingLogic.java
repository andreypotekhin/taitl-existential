package com.taitl.ex.logic.indexing;

import java.io.*;
import com.taitl.ex.core.existential.*;
import com.taitl.existential.*;
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

    public <T> void indexEvent(Event<T> event, T o, Tr tr)
    {
        // TODO: add event to runtimes indexes (e.g. EventField)
    }

    public <T> void indexEvent(Event<T> event, T o, TypeKey<T> type, Tr tr)
    {
        // TODO
    }

    public <T> void indexEvent(BiEvent<T> event, TypeKey<T> type, Tr tr)
    {
        // TODO
    }

    public void close()
    {
    }
}
