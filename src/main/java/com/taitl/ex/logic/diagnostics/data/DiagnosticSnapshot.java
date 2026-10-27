package com.taitl.ex.logic.diagnostics.data;

import com.taitl.existential.diagnostics.*;
import com.taitl.existential.constants.*;
import java.util.*;

/** Owns only detached values; final fields and copied lists guarantee safe publication and serialization. */
public class DiagnosticSnapshot implements Diagnostic
{
    private static final long serialVersionUID = 1L;
    private final DiagnosticCode code;
    private final String message;
    private final String operation;
    private final String rule;
    private final String context;
    private final boolean transactionLocal;
    private final String event;
    private final String type;
    private final String ruleEvent;
    private final String ruleType;
    private final StageName stage;
    private final String boundary;
    private final boolean evaluationStopped;
    private final List<String> notes;
    private final List<String> actions;
    private final String troubleshooting;
    private final List<Diagnostic> children;

    public DiagnosticSnapshot(
            DiagnosticCode code,
            String message,
            String operation,
            String rule,
            String context,
            boolean transactionLocal,
            String event,
            String type,
            String ruleEvent,
            String ruleType,
            StageName stage,
            String boundary,
            boolean evaluationStopped,
            List<String> notes,
            List<String> actions,
            String troubleshooting,
            List<Diagnostic> children)
    {
        this.code = Objects.requireNonNull(code);
        this.message = Objects.requireNonNull(message);
        this.operation = operation;
        this.rule = rule;
        this.context = context;
        this.transactionLocal = transactionLocal;
        this.event = event;
        this.type = type;
        this.ruleEvent = ruleEvent;
        this.ruleType = ruleType;
        this.stage = stage;
        this.boundary = boundary;
        this.evaluationStopped = evaluationStopped;
        this.notes = List.copyOf(notes);
        this.actions = List.copyOf(actions);
        this.troubleshooting = troubleshooting;
        this.children = List.copyOf(children);
    }

    public DiagnosticCode code()
    {
        return code;
    }

    public String message()
    {
        return message;
    }

    public String operation()
    {
        return operation;
    }

    public String rule()
    {
        return rule;
    }

    public String context()
    {
        return context;
    }

    public boolean transactionLocal()
    {
        return transactionLocal;
    }

    public String event()
    {
        return event;
    }

    public String type()
    {
        return type;
    }

    public String ruleEvent()
    {
        return ruleEvent;
    }

    public String ruleType()
    {
        return ruleType;
    }

    public StageName stage()
    {
        return stage;
    }

    public String boundary()
    {
        return boundary;
    }

    public boolean evaluationStopped()
    {
        return evaluationStopped;
    }

    public List<String> notes()
    {
        return notes;
    }

    public List<String> actions()
    {
        return actions;
    }

    public String troubleshooting()
    {
        return troubleshooting;
    }

    public List<Diagnostic> children()
    {
        return children;
    }

}
