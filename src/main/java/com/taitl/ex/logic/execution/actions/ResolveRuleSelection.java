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
 * subject patterns. Sorted membership rows are united by declaration identifier, preserving
 * order and avoiding duplicate execution through overlapping matching paths.
 *
 * Memo-sensitive candidates are checked separately to retain the existing admission policy.
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
        int[] members = mergeRuleMembership.call(rows(inventory, input.types, input.events));
        boolean memoRequired = !input.memoEvents.isEmpty()
                && !rows(inventory, input.memoTypes, input.memoEvents).isEmpty();
        return inventory.selection(members, memoRequired);
    }

    public List<int[]> rows(RuleInventory inventory, Set<TypePattern> types, Set<String> events)
    {
        List<int[]> rows = new ArrayList<>();
        for (TypePattern type : types)
        {
            for (RulePattern pattern : inventory.candidates(type.raw()))
            {
                if (events.contains(pattern.event) && typeMatchingLogic.matches(pattern.type, type))
                {
                    rows.add(pattern.members);
                }
            }
        }
        return rows;
    }

}
