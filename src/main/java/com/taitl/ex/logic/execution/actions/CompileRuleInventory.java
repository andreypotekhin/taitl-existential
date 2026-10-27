package com.taitl.ex.logic.execution.actions;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.ex.logic.execution.data.*;

import java.util.*;

/**
 * Turns indexed rule occurrences into one declaration-ordered inventory.
 * An occurrence reached through multiple index keys is collected once by identity; independent
 * registrations remain distinct even when their callbacks compare equal or are the same object.
 *
 * Sorting the declaration ordinals once assigns contiguous identifiers for later membership
 * merges. Compilation occurs when ConfiguredHandlers publishes a finalized source generation,
 * rather than during each event report.
 */
public class CompileRuleInventory
{
    public RuleInventory call(Map<String, Set<OrderlyEv<?>>> handlers)
    {
        Map<OrderlyEv<?>, String> keys = new IdentityHashMap<>();
        List<OrderlyEv<?>> ordered = new ArrayList<>();
        for (Map.Entry<String, Set<OrderlyEv<?>>> entry : handlers.entrySet())
        {
            for (OrderlyEv<?> handler : entry.getValue())
            {
                if (keys.put(handler, entry.getKey()) == null)
                {
                    ordered.add(handler);
                }
            }
        }
        ordered.sort(Comparator.comparingLong(handler -> handler.ordinal()));
        List<RuleDeclaration> declarations = new ArrayList<>();
        for (OrderlyEv<?> handler : ordered)
        {
            RuleDeclaration declaration =
                    new RuleDeclaration(declarations.size(), keys.get(handler), handler.ev(), handler.effect);
            declaration.origin = handler.origin;
            handler.declaration = declaration;
            declarations.add(declaration);
        }
        return new RuleInventory(declarations);
    }
}
