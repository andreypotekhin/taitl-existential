package com.taitl.existential.configs;

import com.taitl.existential.constants.*;
import com.taitl.existential.evaluables.*;

import java.util.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Configured rules partitioned by execution stage.
 * Owned by Context, Transaction.
 */
public class Stages
{
    protected Map<StageName, List<Evs<?>>> byName = new EnumMap<>(StageName.class);

    public Stages()
    {
        for (StageName stageName : StageName.values())
        {
            byName.put(stageName, new ArrayList<>());
        }
    }

    public <T> void add(StageName stageName, Evs<T> evs)
    {
        sane(stageName, "stageName", evs, "evs");
        byName.get(stageName).add(evs);
    }

    public void addAll(Stages other)
    {
        sane(other, "other");
        for (StageName stageName : StageName.values())
        {
            byName.get(stageName).addAll(other.at(stageName));
        }
    }

    public List<Evs<?>> at(StageName stageName)
    {
        sane(stageName, "stageName");
        return byName.get(stageName);
    }

    public List<Evs<?>> all()
    {
        List<Evs<?>> result = new ArrayList<>();
        for (StageName stageName : StageName.values())
        {
            result.addAll(at(stageName));
        }
        return result;
    }
}
