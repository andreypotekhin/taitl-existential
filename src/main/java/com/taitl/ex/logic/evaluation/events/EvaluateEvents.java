package com.taitl.ex.logic.evaluation.events;

import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.events.actions.*;
import com.taitl.ex.logic.evaluation.events.split_events.*;
import com.taitl.ex.logic.stages.validation.output.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateEvents
{
    protected SplitEvent splitEvent = Creator.singleton(SplitEvent.class);
    protected ExecuteHandlers executeHandlers = Creator.singleton(ExecuteHandlers.class);

    public <T> void call(RuntimeKey<T> runtimeKey, EventField eventField, ValidationReport report,
            boolean useFullEventNames, boolean splitElementaryToCompound)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField", report, "report");
        SplitResult<T> splitResult =
                splitEvent.call(runtimeKey, eventField, useFullEventNames, splitElementaryToCompound);
        executeHandlers.call(splitResult.evaluables(), splitResult.event(), report);
    }
}
