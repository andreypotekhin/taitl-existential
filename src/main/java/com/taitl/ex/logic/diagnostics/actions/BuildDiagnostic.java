package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.ex.logic.diagnostics.data.*;
import com.taitl.existential.diagnostics.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.keys.*;
import java.util.*;

public class BuildDiagnostic
{
    public Diagnostic call(DiagnosticCode code, String operation, RuleOrigin origin, RuntimeKey<?> key,
            StageName stage, String boundary, boolean stopped)
    {
        if (key != null)
        {
            boundary = eventBoundary(stage, boundary);
        }
        String message;
        switch (code)
        {
        case RULE_REJECTED:
            message = "Rule rejected";
            break;
        case INTENT_REJECTED:
            message = "Intent rejected";
            break;
        case INTENT_MISSING:
            message = "No matching intent";
            break;
        default:
            message = "Callback failed";
        }
        List<String> actions = code == DiagnosticCode.INTENT_MISSING
                ? List.of("Define a matching intent for this event/type or review intent gating")
                : List.of();
        return new DiagnosticSnapshot(code, message, operation, origin == null ? null : origin.description,
                origin == null ? null : origin.context, origin != null && origin.transactionLocal,
                key == null ? null : key.event().getClass().getSimpleName(),
                key == null ? null : key.typeKey().toString(),
                origin == null ? null : origin.event, origin == null ? null : origin.type, stage, boundary, stopped,
                stopped ? List.of("Evaluation stopped; later work was not evaluated") : List.of(), actions,
                link(code), List.of());
    }

    public Diagnostic aggregate(String operation, StageName stage, String boundary,
            List<ExistentialException> failures, boolean stopped)
    {
        boundary = eventBoundary(stage, boundary);
        List<Diagnostic> children = new ArrayList<>();
        for (ExistentialException failure : failures)
        {
            Diagnostic child = failure.diagnostic();
            if (child == null)
            {
                child = call(DiagnosticCode.RULE_REJECTED, operation, null, null, stage, boundary, false);
            }
            children.add(child);
        }
        return new DiagnosticSnapshot(DiagnosticCode.EVALUATION_FAILED,
                stage.name().toLowerCase(Locale.ROOT) + " stage failed with " + failures.size() + " error(s)",
                operation, null, null, false, null, null, null, null, stage, boundary, stopped,
                stopped ? List.of("Evaluation stopped; later work was not evaluated") : List.of(), List.of(),
                link(DiagnosticCode.EVALUATION_FAILED), children);
    }

    public Diagnostic rollbackOnly(Diagnostic d)
    {
        List<String> notes = new ArrayList<>(d.notes());
        notes.add("Transaction is rollback-only");
        return new DiagnosticSnapshot(d.code(), d.message(), d.operation(), d.rule(), d.context(), d.transactionLocal(),
                d.event(), d.type(), d.ruleEvent(), d.ruleType(), d.stage(), d.boundary(), d.evaluationStopped(),
                notes, d.actions(), d.troubleshooting(), d.children());
    }

    /** Begin/immediate entity evaluation is triggered by reporting, even within a lifecycle callback. */
    protected String eventBoundary(StageName stage, String boundary)
    {
        return stage == StageName.BEGIN || stage == StageName.IMMEDIATE ? "report" : boundary;
    }

    protected String link(DiagnosticCode code)
    {
        return "/Troubleshooting.md#diagnostic-" + code.id().toLowerCase(Locale.ROOT);
    }
}
