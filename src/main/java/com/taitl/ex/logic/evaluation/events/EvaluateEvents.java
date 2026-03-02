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

    public void call(RuntimeKey<?> runtimeKey, EventField eventField, ValidationReport report,
            boolean useFullEventNames)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField", report, "report");
        SplitResult splitResult = splitEvent.call(runtimeKey, eventField, useFullEventNames);
        executeHandlers.call(splitResult.evaluables(), splitResult.event(), report);
    }
}
