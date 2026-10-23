package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.logic.execution.data.*;

import java.util.*;

/**
 * Unions compiled membership bitmaps using one private accumulator per multirow resolution.
 * Compact declaration identifiers preserve configured order when the result is enumerated.
 * Duplicate discovery cannot multiply a declaration, while separately registered callbacks
 * retain independent bits even when they reference the same handler object.
 *
 * Prepared rows are read-only and safe to share. Zero/one-row cases avoid union allocation;
 * a multirow result is pre-sized to the inventory to avoid growth and never mutates an input.
 * The bitmap goes directly to handler materialization without an intermediate identifier array.
 */
public class MergeRuleMembership
{
    public BitSet call(List<RulePattern> rows, int size)
    {
        if (rows.size() == 1)
        {
            return rows.get(0).members;
        }
        BitSet result = new BitSet(rows.isEmpty() ? 0 : size);
        for (RulePattern row : rows)
        {
            result.or(row.members);
        }
        return result;
    }
}
