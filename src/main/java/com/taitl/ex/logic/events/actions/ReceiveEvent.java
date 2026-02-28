package com.taitl.ex.logic.events.actions;

import com.taitl.ex.logic.events.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.indexing.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class ReceiveEvent
{
    protected EventLogic el;
    protected IndexingLogic indexingLogic;

    public ReceiveEvent(EventLogic el)
    {
        this.el = el;
        this.indexingLogic = el.ev().indexingLogic;
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "type", t, "t", type, "type", tr, "tr");
        evaluateIntent(RuntimeKey.valueOf(event, type, t, useFullClassNames()), tr);
        // Add event to indexes for late-stage processing
        indexingLogic.indexEvent(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "type", type, "type", tr, "tr");
        T entity = event.t1 != null ? event.t1 : event.t0;
        evaluateIntent(RuntimeKey.valueOf(event, type, entity, useFullClassNames()), tr);
        indexingLogic.indexEvent(event, type, tr);
    }

    protected <T> void evaluateIntent(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        evaluationLogic().evaluateIntent(tr, runtimeKey);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return el.ev().ex().transactions().logic().evaluationLogic;
    }

    protected boolean useFullClassNames()
    {
        return el.ev().ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }
}
