package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Owns handler selections for one operation, stage, and configured-rule generation.
 * Dispatch first probes a compact signature; only a miss expands event families and structural
 * types, then publishes an immutable ordered RuleSelection. Empty matches are cached too.
 * The legacy MultiKey adapter shares the same bounded entry budget.
 *
 * Inventory identity invalidates prepared selections after a rule-source change. Concurrent
 * readers share published selections without retaining reported payloads or memo values.
 * Close clears resident selections after the owner's evaluation work has drained.
 */
public class EventField
{
    @Up
    protected ConfigurationIndexes ci;

    @Logic
    protected ConfiguredHandlers configured;

    @Logic
    protected ResolveRuleSelection resolveRuleSelection = Creator.create(ResolveRuleSelection.class);

    protected volatile PreparedSelections prepared;

    public EventField(ConfigurationIndexes ci)
    {
        this(ci, ci != null ? ci.configuredHandlers : null);
    }

    public EventField(ConfigurationIndexes ci, ConfiguredHandlers configured)
    {
        sane(configured, "configured");
        this.ci = ci;
        this.configured = configured;
    }

    protected ConfiguredHandlers source()
    {
        return configured;
    }

    protected PreparedSelections prepared()
    {
        RuleInventory inventory = source().inventory();
        PreparedSelections current = prepared;
        if (current == null || current.inventory != inventory)
        {
            synchronized (this)
            {
                current = prepared;
                if (current == null || current.inventory != inventory)
                {
                    current = new PreparedSelections(inventory);
                    prepared = current;
                }
            }
        }
        return current;
    }

    public RuleSelection select(DispatchSignature signature, Supplier<SelectionInput> input)
    {
        PreparedSelections current = prepared();
        return current.entries.resolve(signature, () -> resolveRuleSelection.call(current.inventory, input.get()));
    }

    public <T> List<Ev<T>> get(MultiKey<T> multiKey)
    {
        return selection(multiKey).handlers();
    }

    public <T> RuleSelection selection(MultiKey<T> multiKey)
    {
        sane(multiKey, "multiKey");
        PreparedSelections current = prepared();
        return current.entries.resolve(multiKey.toString(), () -> legacySelection(multiKey));
    }

    protected <T> RuleSelection legacySelection(MultiKey<T> multiKey)
    {
        Set<OrderlyEv<?>> unique = Collections.newSetFromMap(new IdentityHashMap<>());
        for (EventKey<T> eventKey : multiKey.eventKeys())
        {
            Set<OrderlyEv<?>> handlers = source().get(eventKey);
            if (handlers != null)
            {
                unique.addAll(handlers);
            }
        }
        List<OrderlyEv<?>> ordered = new ArrayList<>(unique);
        ci.maintainGlobalOrder.sort(ordered);
        List<RuleDeclaration> handlers = new ArrayList<>();
        for (OrderlyEv<?> handler : ordered)
        {
            handlers.add(handler.declaration);
        }
        return new RuleSelection(handlers.toArray(new RuleDeclaration[0]), false);
    }

    public <T> boolean hasBiEventKey(EventKey<T> eventKey)
    {
        sane(eventKey, "eventKey");
        return ci.hasBiKey(eventKey);
    }

    public int cachedSelections()
    {
        PreparedSelections current = prepared;
        return current == null ? 0 : current.entries.size();
    }

    public boolean hasEffects()
    {
        return source().hasEffects();
    }

    public void close()
    {
        PreparedSelections current = prepared;
        if (current != null)
        {
            current.entries.clear();
        }
        prepared = null;
    }
}
