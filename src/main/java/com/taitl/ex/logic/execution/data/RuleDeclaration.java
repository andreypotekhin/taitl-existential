package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.evaluables.*;

/**
 * One configured event rule occurrence in declaration order. Its compact identifier joins
 * matching type rows, while its handler and effect marker let the final selection preserve both
 * callback order and the evaluation policy needed when a selected effect is present. Independent
 * declarations retain separate identifiers even when they use the same handler object.
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
    public boolean effect;

    public RuleDeclaration(int id, String key, Ev<?> handler)
    {
        this(id, key, handler, false);
    }

    public RuleDeclaration(int id, String key, Ev<?> handler, boolean effect)
    {
        int open = key.indexOf('<');
        this.id = id;
        this.event = key.substring(0, open);
        this.type = new TypePattern(key.substring(open + 1, key.length() - 1));
        this.handler = handler;
        this.effect = effect;
    }
}
