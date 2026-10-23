package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.evaluables.*;

/**
 * One configured rule occurrence with a compact identifier in declaration order.
 * Its event name and parsed subject pattern support selection, while its handler is the
 * original configured callback. Independent registrations retain separate identifiers even
 * when they use the same callback object.
 *
 * Identifiers are local to a compiled RuleInventory and must not be compared across generations.
 * They let matching paths deduplicate one declaration without invoking callback equality.
 */
public class RuleDeclaration
{
    public int id;
    public String event;
    public TypePattern type;
    public Ev<?> handler;

    public RuleDeclaration(int id, String key, Ev<?> handler)
    {
        int open = key.indexOf('<');
        this.id = id;
        this.event = key.substring(0, open);
        this.type = new TypePattern(key.substring(open + 1, key.length() - 1));
        this.handler = handler;
    }
}
