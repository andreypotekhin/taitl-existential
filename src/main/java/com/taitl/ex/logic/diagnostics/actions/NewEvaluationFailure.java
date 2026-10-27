package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.existential.exceptions.*;

/** Internal origin markers permit copying stock failures while retaining caller-owned exceptions as causes. */
public class NewEvaluationFailure
{
    public static ConditionNotMetException condition(String message)
    {
        return new Condition(message);
    }

    public static PredicateFailure predicate(String message)
    {
        return new Predicate(message);
    }

    public static EventHandlerException handler(String message)
    {
        return new Handler(message, null);
    }

    public static EventHandlerException handler(String message, Throwable cause)
    {
        return new Handler(message, cause);
    }

    static class Condition extends ConditionNotMetException implements LibraryFailure
    {
        Condition(String message)
        {
            super(message);
        }
    }

    static class Predicate extends PredicateFailure implements LibraryFailure
    {
        Predicate(String message)
        {
            super(message);
        }
    }

    static class Handler extends EventHandlerException implements LibraryFailure
    {
        Handler(String message, Throwable cause)
        {
            super(message, cause);
        }
    }
}
