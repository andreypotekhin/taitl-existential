package com.taitl.ex.logic.evaluation.actions;

import com.taitl.ex.logic.configuration.indexes.data.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

public class AtEvent
{
    protected SplitEvent splitEvent = new SplitEvent();

    public Slice call(RuntimeKey<?> runtimeKey, EventField eventField)
    {
        sane(runtimeKey, "runtimeKey", eventField, "eventField");
        Set<RuntimeKey<?>> splitKeys = split(runtimeKey);
        MultiKey multiKey = multiKey(splitKeys);
        List<Ev<?>> evs = eventField.get(multiKey);
        return new Slice(splitKeys, evs);
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    protected Set<RuntimeKey<?>> split(RuntimeKey<?> runtimeKey)
    {
        return (Set) splitEvent.split((RuntimeKey) runtimeKey);
    }

    protected MultiKey multiKey(Set<RuntimeKey<?>> splitKeys)
    {
        sane(splitKeys, "splitKeys");
        EventKey[] eventKeys = splitKeys.stream().map(RuntimeKey::key).toArray(EventKey[]::new);
        return MultiKey.valueOf(eventKeys);
    }

    public static class Slice
    {
        protected final Set<RuntimeKey<?>> splitKeys;
        protected final List<Ev<?>> evs;

        public Slice(Set<RuntimeKey<?>> splitKeys, List<Ev<?>> evs)
        {
            sane(splitKeys, "splitKeys", evs, "evs");
            this.splitKeys = splitKeys;
            this.evs = evs;
        }

        public Set<RuntimeKey<?>> splitKeys()
        {
            return splitKeys;
        }

        public List<Ev<?>> evs()
        {
            return evs;
        }
    }
}
