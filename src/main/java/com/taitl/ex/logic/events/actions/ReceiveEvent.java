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

    public ReceiveEvent(EventLogic el)
    {
        this.el = el;
    }

    public <T> void event(Event<T> event, T t, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "event", t, "t", type, "type", tr, "tr");
        RuntimeKey<T> runtimeKey = RuntimeKey.valueOf(event, type, t, useFullClassNames());
        evaluateStages(runtimeKey, tr);
        // Add event to indexes for late-stage processing
        indexingLogic().indexEvent(event, t, type, tr);
    }

    public <T> void event(BiEvent<T> event, TypeKey<T> type, Tr tr) throws ExistentialException
    {
        sane(event, "event", type, "type", tr, "tr");
        T entity = event.t1 != null ? event.t1 : event.t0;
        RuntimeKey<T> runtimeKey = RuntimeKey.valueOf(event, type, entity, useFullClassNames());
        evaluateStages(runtimeKey, tr);
        indexingLogic().indexEvent(event, type, tr);
    }

    protected <T> void evaluateStages(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr");
        transactionLogic().preconditionLogic.onEvent(runtimeKey, tr);
        transactionLogic().immediateLogic.onEvent(runtimeKey, tr);
    }

    protected EvaluationLogic evaluationLogic()
    {
        return el.ev().ex().transactions().logic().evaluationLogic;
    }

    protected com.taitl.ex.logic.transactions.TransactionLogic transactionLogic()
    {
        return el.ev().ex().transactions().logic();
    }

    protected boolean useFullClassNames()
    {
        return el.ev().ex().get(Flags.TYPE_KEYS_USE_FULL_CLASS_NAMES);
    }

    protected IndexingLogic indexingLogic()
    {
        return el.ev().indexingLogic;
    }
}
