package com.taitl.ex.logic.diagnostics.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.existential.diagnostics.*;
import java.util.*;

public class RenderDiagnostic
{
    @Logic
    protected EscapeDiagnosticText escape = Creator.create(EscapeDiagnosticText.class);

    public String call(Diagnostic diagnostic)
    {
        StringBuilder text = new StringBuilder();
        render(text, diagnostic, null, "");
        return text.toString();
    }

    protected void render(StringBuilder text, Diagnostic d, Diagnostic parent, String indent)
    {
        text.append(indent).append("error[").append(d.code().id()).append("]: ").append(escape.call(d.message()));
        if (parent == null || !Objects.equals(d.operation(), parent.operation()))
        {
            field(text, indent, "operation", d.operation());
        }
        field(text, indent, "rule", d.rule());
        field(text, indent, "context", d.transactionLocal() ? "transaction-local" : d.context());
        field(text, indent, "event", event(d.event(), d.type()));
        if (parent == null || d.stage() != parent.stage() || !Objects.equals(d.boundary(), parent.boundary()))
        {
            String evaluation = d.stage() == null ? null : d.stage().name().toLowerCase(Locale.ROOT);
            if (d.boundary() != null)
            {
                evaluation = evaluation == null ? d.boundary() : evaluation + " during " + d.boundary();
            }
            field(text, indent, "evaluation", evaluation);
        }
        if (d.ruleEvent() != null
                && (!Objects.equals(d.ruleEvent(), d.event()) || !Objects.equals(d.ruleType(), d.type())))
        {
            field(text, indent, "selected rule", event(d.ruleEvent(), d.ruleType()));
        }
        for (String note : d.notes())
        {
            if (parent == null || !parent.notes().contains(note))
            {
                field(text, indent, "note", note);
            }
        }
        for (String action : d.actions())
        {
            field(text, indent, "action", action);
        }
        field(text, indent, "see", d.troubleshooting());
        for (Diagnostic child : d.children())
        {
            text.append('\n');
            render(text, child, d, indent + "  ");
        }
    }

    protected String event(String event, String type)
    {
        return event == null ? null : event + (type == null ? "" : "<" + type + ">");
    }

    protected void field(StringBuilder text, String indent, String label, String value)
    {
        if (value != null && !value.isEmpty())
        {
            text.append('\n').append(indent).append("  ").append(label).append(": ").append(escape.call(value));
        }
    }
}
