package com.taitl.ex.logic.evaluation.events;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.ex.logic.evaluation.events.split_events.data.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateEvents
{
    @Logic
    protected SplitEvent splitEvent = Creator.singleton(SplitEvent.class);

    @Logic
    protected ExecuteHandlers executeHandlers = Creator.singleton(ExecuteHandlers.class);

    public <T> void call(RuntimeKey<T> runtimeKey, Tr tr, EventField eventField, ValidationReport report,
            boolean useFullEventNames, boolean splitElementaryToCompound)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", tr, "tr", eventField, "eventField", report, "report");
        SplitResult<T> splitResult =
                splitEvent.call(runtimeKey, eventField, useFullEventNames, splitElementaryToCompound, tr);
        executeHandlers.call(splitResult.evaluables(), splitResult.event(), report);
    }
}
