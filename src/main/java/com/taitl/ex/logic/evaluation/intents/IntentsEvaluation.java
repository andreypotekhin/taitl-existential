package com.taitl.ex.logic.evaluation.intents;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.evaluation.*;
import com.taitl.ex.logic.evaluation.intents.actions.*;
import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Orchestrates immediate evaluation of configured intents for a runtime event.
 */
public class IntentsEvaluation
{
    protected final EvaluationLogic el;
    protected final EventSplitter eventSplitter;
    protected final GroupByEventType groupByEventType;
    protected final EvaluateIntentGroup evaluateIntentGroup;

    public IntentsEvaluation(EvaluationLogic el)
    {
        sane(el, "el");
        this.el = el;
        this.eventSplitter = Creator.singleton(EventSplitter.class);
        this.groupByEventType = new GroupByEventType();
        this.evaluateIntentGroup = new EvaluateIntentGroup();
    }

    public <T> void call(RuntimeKey<T> runtimeKey, Tr tr) throws ExistentialException
    {
        sane(tr, "tr", runtimeKey, "runtimeKey");
        ConfigurationIndexes indexes = el.config(tr).indexes();
        if (!indexes.hasIntentEventTypes() && !tr.hasIntentEventTypes())
        {
            return;
        }

        Map<String, List<RuntimeKey<T>>> grouped = groupByEventType.call(eventSplitter.split(runtimeKey));

        for (Map.Entry<String, List<RuntimeKey<T>>> entry : grouped.entrySet())
        {
            evaluateIntentGroup.call(entry.getKey(), entry.getValue(), indexes, tr);
        }
    }
}
