package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;

import java.util.*;

/**
 * A configured event/subject pattern and the ordered declarations that use it.
 * Matching the pattern once can select multiple independent rule occurrences, avoiding repeated
 * type checks for equivalent declarations. Membership identifiers refer to one RuleInventory.
 *
 * The supplied membership array is copied and treated as immutable after compilation.
 * Pattern sharing merges selection work without merging distinct callback registrations.
 */
public class RulePattern
{
    public String event;
    public TypePattern type;
    public int[] members;

    public RulePattern(RuleDeclaration declaration, int[] members)
    {
        event = declaration.event;
        type = declaration.type;
        this.members = members.clone();
    }
}
