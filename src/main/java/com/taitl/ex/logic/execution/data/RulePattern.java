package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;

import java.util.*;

/**
 * A configured event/subject pattern and the ordered declarations that use it.
 * Matching the pattern once can select multiple independent rule occurrences, avoiding repeated
 * type checks for equivalent declarations. Membership identifiers refer to one RuleInventory.
 *
 * Compilation converts sorted identifiers into a pre-sized bitmap, then attaches the single-row
 * ordered selection before publication. Both are read-only thereafter. Single-row dispatch can
 * reuse that selection without rebuilding handlers; multirow dispatch uses a private accumulator.
 * Pattern sharing merges selection work without merging distinct callback registrations.
 */
public class RulePattern
{
    public String event;
    public TypePattern type;
    public BitSet members;
    public RuleSelection selection;

    public RulePattern(RuleDeclaration declaration, int[] members)
    {
        event = declaration.event;
        type = declaration.type;
        this.members = new BitSet(members.length == 0 ? 0 : members[members.length - 1] + 1);
        for (int id : members)
        {
            this.members.set(id);
        }
    }
}
