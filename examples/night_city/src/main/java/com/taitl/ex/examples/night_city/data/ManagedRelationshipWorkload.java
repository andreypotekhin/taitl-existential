package com.taitl.ex.examples.night_city.data;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.transactions.*;
import java.util.*;

public class ManagedRelationshipWorkload
{
    private static final TypeKey<Dwelling<Cat, Rugs>> HOME = new TypeKey<Dwelling<Cat, Rugs>>() {
    };
    public final List<Cat> cats = new ArrayList<>();
    public final List<Dwelling<Cat, Rugs>> homes = new ArrayList<>();

    public ManagedRelationshipWorkload(int count, int groups)
    {
        if (count < 1 || groups < 1 || groups > count)
        {
            throw new IllegalArgumentException("Require positive entities and groups <= entities");
        }
        for (int i = 0; i < groups; i++)
        {
            homes.add(new Dwelling<>("Grey", "Zone-" + i));
        }
        for (int i = 0; i < count; i++)
        {
            cats.add(new Cat("Black", "Zone-" + (i % groups)));
        }
    }

    public void report(Tr tr, int repeats)
    {
        for (Dwelling<Cat, Rugs> home : homes)
        {
            tr.read(home, HOME);
        }
        for (Cat cat : cats)
        {
            for (int i = 0; i < repeats; i++)
            {
                tr.create(cat);
            }
        }
    }

    public void reportManual(Tr tr, int repeats, MultiJoin<Cat, Dwelling<Cat, ?>, String> join)
    {
        for (Dwelling<Cat, Rugs> home : homes)
        {
            join.addRight(home);
            tr.read(home, HOME);
        }
        for (Cat cat : cats)
        {
            for (int i = 0; i < repeats; i++)
            {
                join.addLeft(cat);
                tr.create(cat);
            }
        }
    }

}
