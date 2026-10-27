package com.taitl.ex.examples.night_city.app;

import com.taitl.ex.examples.night_city.configure.*;
import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;

import java.util.*;
import java.util.logging.*;

public class ManagedCityStory
{
    private static final String OP = "/night-city/managed/cats";
    private static final Logger LOGGER = Logger.getLogger(ManagedCityStory.class.getName());

    public static void main(String[] args)
    {
        run().forEach(outcome -> LOGGER.info(outcome));
    }

    public static List<String> run()
    {
        try (Existential ex = new Existential())
        {
            Existential previous = Ex.instance(ex);
            try
            {
                IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> homes =
                        new ConfigureManagedRelationships().configure(OP);
                Dwelling<Cat, Rugs> park = new Dwelling<>("Grey", "Park");
                Dwelling<Cat, Rugs> garden = new Dwelling<>("Red", "Garden");
                create(ex, homes, new Cat("Grey", "Park"), park, true);
                Cat cat = new Cat("Black", "Park");
                create(ex, homes, cat, park, false);
                relocate(ex, homes, cat, new Cat("Black", "Garden"), List.of(park, garden));
                return List.of("Rejected a non-black cat and rolled back explicitly.",
                        "Created a cat using seeded transaction-local dwellings.",
                        "Maintained relocation relationships through checkpoint and commit.");
            }
            finally
            {
                Ex.instance(previous);
            }
        }
    }

    private static void create(Existential ex, IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> homes,
            Cat cat, Dwelling<Cat, ?> home, boolean reject)
    {
        Tr tr = ex.begin(OP);
        try
        {
            tr.index(homes).addAllRight(List.of(home));
            tr.create(cat);
            if (reject)
            {
                try
                {
                    tr.commit();
                }
                catch (ValidationStageExceptions expected)
                {
                    if (expected.getCause() == null
                            || !expected.getCause().getMessage().contains("Cats are born black"))
                    {
                        throw expected;
                    }
                    tr.rollback();
                    return;
                }
                throw new IllegalStateException("Expected birth rule rejection");
            }
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }

    private static void relocate(Existential ex, IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> homes,
            Cat before, Cat after, List<Dwelling<Cat, Rugs>> dwellings)
    {
        Tr tr = ex.begin(OP);
        try
        {
            tr.index(homes).addAllLeft(List.of(before));
            for (Dwelling<Cat, Rugs> dwelling : dwellings)
            {
                tr.read(dwelling, new TypeKey<Dwelling<Cat, Rugs>>() {
                });
            }
            tr.transit(before, after);
            tr.checkpoint();
            if (tr.index(homes).left().containsKey(before)
                    || !tr.index(homes).left().containsKey(after))
            {
                throw new IllegalStateException("Relocation did not retire its prior relationship");
            }
            tr.commit();
        }
        finally
        {
            tr.close();
        }
    }
}
