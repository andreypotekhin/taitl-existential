package com.taitl.ex.logic.diagnostics.data;

import com.taitl.existential.diagnostics.*;

/** Preserves the original fallback without forcing rendering when a boundary adds a consequence note. */
public class FailureSnapshot extends DiagnosticSnapshot
{
    public final String fallback;

    public FailureSnapshot(Diagnostic d, String fallback)
    {
        super(d.code(), d.message(), d.operation(), d.rule(), d.context(), d.transactionLocal(), d.event(), d.type(),
                d.ruleEvent(), d.ruleType(), d.stage(), d.boundary(), d.evaluationStopped(), d.notes(), d.actions(),
                d.troubleshooting(), d.children());
        this.fallback = fallback;
    }
}
