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
        List<Ev<?>> handlers = new ArrayList<>(members.length);
        for (int id : members)
        {
            handlers.add(declarations.get(id).handler);
        }
        return new RuleSelection(handlers, memoRequired);
    }

    public int size()
    {
        return declarations.size();
    }
}
