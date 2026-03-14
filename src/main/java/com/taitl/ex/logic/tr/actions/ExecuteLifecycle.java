package com.taitl.ex.logic.tr.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.concrete.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.ex.logic.tr.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.*;
import com.taitl.existential.handlers.types.*;

import static com.taitl.ex.common.helper.Args.*;

public class ExecuteLifecycle
{
    @Up
    protected final ConcreteTr tr;

    @Logic
    protected final IntentLogic intentLogic;

    public ExecuteLifecycle(ConcreteTr tr, IntentLogic intentLogic)
    {
        sane(tr, "tr", intentLogic, "intentLogic");
        this.tr = tr;
        this.intentLogic = intentLogic;
    }

    public void call(Class<?> eventClass, StageName stageName) throws ExistentialException
    {
        sane(eventClass, "eventClass", stageName, "stageName");
        for (Transaction transaction : tr.transactions())
        {
            call(eventClass, stageName, transaction);
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected void call(Class<?> eventClass, StageName stageName, Transaction transaction)
            throws ExistentialException
    {
        sane(eventClass, "eventClass", stageName, "stageName", transaction, "transaction");
        for (Evs<?> evs : transaction.stage().at(stageName))
        {
            if (!(evs instanceof Life<?>))
            {
                continue;
            }
            Life<?> life = (Life<?>) evs;
            if (!intentLogic.matchesType(life.typeKey(), transaction))
            {
                continue;
            }
            for (Ev<?> ev : life.list())
            {
                if (!(ev instanceof EventHandler<?>))
                {
                    continue;
                }
                EventHandler<?> eventHandler = (EventHandler<?>) ev;
                if (!eventClass.equals(eventHandler.eventType().eventClass()))
                {
                    continue;
                }
                if (!(eventHandler instanceof On<?>))
                {
                    throw new IllegalStateException("Lifecycle handler must extend On, got "
                            + eventHandler.getClass().getName());
                }
                ExecuteHandler.handle((On) eventHandler, transaction);
            }
        }
    }
}
