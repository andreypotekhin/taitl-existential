package com.taitl.ex.logic.diagnostics.data;

/** Configuration-owned declaration values; never invocation state or a callback. */
public class RuleOrigin
{
    public final String description;
    public final String context;
    public final boolean transactionLocal;
    public final String event;
    public final String type;

    public RuleOrigin(String description, String context, boolean transactionLocal, String key)
    {
        this.description = description == null || description.isEmpty() ? null : description;
        this.context = context;
        this.transactionLocal = transactionLocal;
        int open = key.indexOf('<');
        this.event = key.substring(0, open);
        this.type = key.substring(open + 1, key.length() - 1);
    }
}
