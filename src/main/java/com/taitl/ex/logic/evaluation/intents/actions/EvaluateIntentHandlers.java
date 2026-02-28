package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.existential.events.types.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.handlers.types.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class EvaluateIntentHandlers
{
    protected final EvaluateIntent evaluateIntent;

    public EvaluateIntentHandlers()
    {
        this(new EvaluateIntent());
    }

    protected EvaluateIntentHandlers(EvaluateIntent evaluateIntent)
    {
        sane(evaluateIntent, "evaluateIntent");
        this.evaluateIntent = evaluateIntent;
    }

    public boolean allowed(List<EventHandler<?>> intents, Event<?> event) throws ExistentialException
    {
        sane(intents, "intents", event, "event");
        for (EventHandler<?> intent : intents)
        {
            if (evaluateIntent.call(intent, event))
            {
                return true;
            }
        }
        return false;
    }
}
