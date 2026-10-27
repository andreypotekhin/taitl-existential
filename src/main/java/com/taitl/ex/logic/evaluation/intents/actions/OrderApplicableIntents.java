package com.taitl.ex.logic.evaluation.intents.actions;

import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.handlers.types.*;
import com.taitl.existential.transactions.*;

import java.util.*;

/**
 * Restores declaration order after matching several event families, including general On intents.
 * Shared order comes from EventField's cached merged selection; only unmatched local declarations
 * need traversal. Identity removes repeated discovery without retaining an invocation's payload.
 */
public class OrderApplicableIntents
{
    public List<EventHandler<?>> call(List<EventHandler<?>> applicable, List<? extends Ev<?>> shared,
            Tr tr, StageName stage)
    {
        Set<EventHandler<?>> remaining = Collections.newSetFromMap(new IdentityHashMap<>());
        remaining.addAll(applicable);
        List<EventHandler<?>> ordered = new ArrayList<>(applicable.size());
        for (Ev<?> ev : shared)
        {
            if (ev instanceof EventHandler<?> && remaining.remove(ev))
            {
                ordered.add((EventHandler<?>) ev);
            }
        }
        if (remaining.isEmpty())
        {
            return ordered;
        }
        for (Transaction transaction : tr.transactions())
        {
            for (Evs<?> rules : transaction.stage().at(stage))
            {
                if (rules instanceof Intent<?>)
                {
                    for (Ev<?> ev : rules.list())
                    {
                        if (ev instanceof EventHandler<?> && remaining.remove(ev))
                        {
                            ordered.add((EventHandler<?>) ev);
                        }
                    }
                }
            }
        }
        for (EventHandler<?> handler : applicable)
        {
            if (remaining.remove(handler))
            {
                ordered.add(handler);
            }
        }
        return ordered;
    }
}
