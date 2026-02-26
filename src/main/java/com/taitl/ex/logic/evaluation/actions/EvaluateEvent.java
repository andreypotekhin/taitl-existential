package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.evaluation.split_events.*;
import com.taitl.ex.logic.validation.output.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateEvent
{
    protected SplitEvent splitEvent = new SplitEvent();
    protected ExecuteHandlers executeHandlers = new ExecuteHandlers();

    public void call(RuntimeKey<?> runtimeKey, EventField eventField, ValidationReport report)
            throws ExistentialException
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        SplitResult splitResult = splitEvent.call(runtimeKey, eventField);
        executeHandlers.call(splitResult.evs(), splitResult.event(), report);
    }
}
