package com.taitl.ex.logic.execution.data;

import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.evaluables.*;

import java.util.*;

/**
 * A compiled, ordered inventory for one finalized rule source.
 * Declarations receive contiguous identifiers in configured order. Rules sharing an event and
 * subject pattern share a membership row, and raw-type buckets narrow cold matching candidates.
 *
 * Matching policy belongs to ResolveRuleSelection. This inventory translates its sorted
 * identifier result into an immutable RuleSelection without sorting callbacks again.
 * Native bitmap and iterator materializers avoid intermediate identifier arrays. Every materializer
 * transfers an exclusively owned handler array to its selection. Each pattern also owns a
 * precompiled single-row selection, allowing that common case to bypass materialization.
 * Its rows and declarations are treated as read-only after publication and carry no event payload.
 */
public class RuleInventory
{
    protected List<RuleDeclaration> declarations;
    protected Map<String, List<RulePattern>> buckets = new HashMap<>();

    public RuleInventory(List<RuleDeclaration> declarations)
    {
        this.declarations = declarations;
        Map<String, List<RuleDeclaration>> patterns = new LinkedHashMap<>();
        for (RuleDeclaration rule : declarations)
        {
            String key = rule.event + "<" + rule.type + ">";
            patterns.computeIfAbsent(key, ignored -> new ArrayList<>()).add(rule);
        }
        for (List<RuleDeclaration> rules : patterns.values())
        {
            int[] ids = new int[rules.size()];
            for (int i = 0; i < ids.length; i++)
            {
                ids[i] = rules.get(i).id;
            }
            RulePattern pattern = new RulePattern(rules.get(0), ids);
            pattern.selection = selection(pattern.members, false);
            buckets.computeIfAbsent(pattern.type.raw(), key -> new ArrayList<>()).add(pattern);
        }
    }

    public List<RulePattern> candidates(String raw)
    {
        return buckets.getOrDefault(raw, Collections.emptyList());
    }

    public RuleSelection selection(int[] members)
    {
        return selection(members, false);
    }

    public RuleSelection selection(int[] members, boolean memoRequired)
    {
        Ev<?>[] handlers = new Ev<?>[members.length];
        for (int i = 0; i < members.length; i++)
        {
            handlers[i] = declarations.get(members[i]).handler;
        }
        return new RuleSelection(handlers, memoRequired);
    }

    /** Materializes native bitmap membership without an intermediate identifier array. */
    public RuleSelection selection(BitSet members, boolean memoRequired)
    {
        Ev<?>[] handlers = new Ev<?>[members.cardinality()];
        int count = 0;
        for (int id = members.nextSetBit(0); id >= 0; id = members.nextSetBit(id + 1))
        {
            handlers[count++] = declarations.get(id).handler;
        }
        return new RuleSelection(handlers, memoRequired);
    }

    /** Adapts an alternative native membership iterator without a runtime dependency on its representation. */
    public RuleSelection selection(PrimitiveIterator.OfInt members, int size, boolean memoRequired)
    {
        Ev<?>[] handlers = new Ev<?>[size];
        for (int i = 0; i < size; i++)
        {
            handlers[i] = declarations.get(members.nextInt()).handler;
        }
        return new RuleSelection(handlers, memoRequired);
    }

    public int size()
    {
        return declarations.size();
    }
}
