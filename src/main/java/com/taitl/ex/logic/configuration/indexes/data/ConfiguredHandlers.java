package com.taitl.ex.logic.configuration.indexes.data;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.execution.data.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.configuration.indexes.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Stores configured rule occurrences under their rendered event/subject keys.
 * Each registration receives an OrderlyEv carrying its declaration ordinal; separate
 * registrations remain separate even when they refer to the same callback object.
 *
 * After indexing is finalized, a lazily published RuleInventory compiles the occurrences into
 * compact declaration identifiers and raw-type pattern buckets. Adding a declaration invalidates
 * that inventory so EventField can replace its selection generation. Legacy key lookup remains
 * available alongside compiled selection. Clear releases handlers and resets publication state.
 */
public class ConfiguredHandlers
{
    @Up
    protected ConfigurationIndexes ci;

    @Logic
    protected CompileRuleInventory compileRuleInventory = Creator.create(CompileRuleInventory.class);

    protected volatile RuleInventory inventory;

    /** EventKey to Set<OrderlyEv<>> **/
    protected SetMap<String, OrderlyEv<?>> handlers = new SetMap<>();

    /** Is indexing done? */
    protected volatile boolean ready = false;
    protected volatile boolean effects = false;

    public ConfiguredHandlers(ConfigurationIndexes ci)
    {
        this.ci = ci;
    }

    /**
     * Gets event handlers for the specified event key.
     *
     * @param key
     *            TypeKey to search for.
     * @return Set<Ev<T>>, or null if no handlers defined for the type.
     */
    public <T> Set<OrderlyEv<?>> get(EventKey<T> key)
    {
        sane(key, "key");
        Set<OrderlyEv<?>> result = handlers.get(key.toString());
        if (result != null && result.isEmpty())
        {
            result = null;
        }
        return result;
    }

    public <T> boolean contains(EventKey<T> key)
    {
        sane(key, "key");
        return handlers.containsKey(key.toString());
    }

    public <T> Set<OrderlyEv<?>> put(EventKey<T> key, Ev<T> value)
    {
        return put(key, value, false);
    }

    public <T> Set<OrderlyEv<?>> put(EventKey<T> key, Ev<T> value, boolean effect)
    {
        sane(key, "key");
        sane(value, "value");
        check(value.single(), "Only single event handlers (Ev<T>) are supported,"
                + " but the passed-in is a compound one (like Evs<T>).");
        synchronized (this)
        {
            OrderlyEv<T> orderedValue = ci.maintainGlobalOrder.globallyOrdered(value, effect);
            inventory = null;
            effects |= effect;
            return handlers.add(key.toString(), orderedValue);
        }
    }

    /**
     * Is indexing finalized?
     */
    public boolean ready()
    {
        return ready;
    }

    public boolean hasHandlers()
    {
        return !handlers.isEmpty();
    }

    public boolean hasEffects()
    {
        return effects;
    }

    public void ready(boolean ready)
    {
        this.ready = ready;
    }

    public RuleInventory inventory()
    {
        RuleInventory current = inventory;
        if (current == null)
        {
            synchronized (this)
            {
                current = inventory;
                if (current == null)
                {
                    verify(ready, "Configured handlers index is not ready");
                    current = compileRuleInventory.call(handlers);
                    inventory = current;
                }
            }
        }
        return current;
    }

    public synchronized void clear()
    {
        inventory = null;
        ready = false;
        effects = false;
        handlers.clear();
    }
}
