package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.logic.types.*;
import com.taitl.ex.logic.types.data.*;

import java.util.*;

/**
 * Resolves a cold dispatch against one compiled RuleInventory.
 * Raw-type buckets restrict candidates before structural type matching checks their event and
 * subject patterns. A pattern reached through several type paths is matched once. Compiled
 * bitmap rows are united by declaration identifier, preserving
 * order and avoiding duplicate execution through overlapping matching paths.
 *
 * Memo-sensitive candidates are checked separately to retain the existing admission policy.
 * A single prepared row reuses its precompiled selection when memo admission permits it.
 * The result is an immutable payload-free selection suitable for the owning EventField cache;
 * entity-specific memo values are still obtained during execution.
 */
public class ResolveRuleSelection
{
    @Logic
    protected MergeRuleMembership mergeRuleMembership = Creator.create(MergeRuleMembership.class);

    @Logic
    protected TypeMatchingLogic typeMatchingLogic = Creator.create(TypeMatchingLogic.class);

    public RuleSelection call(RuleInventory inventory, SelectionInput input)
    {
        List<RulePattern> rows = rows(inventory, input.types, input.events);
        boolean memoRequired = !input.memoEvents.isEmpty()
                && !rows(inventory, input.memoTypes, input.memoEvents).isEmpty();
        if (rows.size() == 1 && !memoRequired)
        {
            return rows.get(0).selection;
        }
        return inventory.selection(mergeRuleMembership.call(rows, inventory.size()), memoRequired);
    }

    public List<RulePattern> rows(RuleInventory inventory, Set<TypePattern> types, Set<String> events)
    {
        List<RulePattern> rows = new ArrayList<>();
        Set<RulePattern> selected = Collections.newSetFromMap(new IdentityHashMap<>());
        for (TypePattern type : types)
        {
            for (RulePattern pattern : inventory.candidates(type.raw()))
            {
                if (!selected.contains(pattern) && events.contains(pattern.event)
                        && typeMatchingLogic.matches(pattern.type, type))
                {
                    selected.add(pattern);
                    rows.add(pattern);
                }
            }
        }
        return rows;
    }

}
