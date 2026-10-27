package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.keys.*;

public class ConfigureManagedRelationships
{
    public IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> configure(String context)
    {
        return configure(context, IndexingPolicy.inherit());
    }

    public IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> configure(String context, IndexingPolicy policy)
    {
        new ConfigureCats().configureState(context);
        IndexDefinition<MultiJoin<Cat, Dwelling<Cat, ?>, String>> homes = IndexDefinition.multiJoin(
                "catDwellings", new TypeKey<Cat>(Cat.class), new TypeKey<Dwelling<Cat, ?>>() {
                },
                cat -> cat.location(), dwelling -> dwelling.location()).withPolicy(policy);
        // @formatter:off
        Ex.configure().context(context)
                .index(homes)
                .invariant(Cat.class)
                    .exists(homes, join -> join.left(), "Every cat must have a dwelling at its location");
        // @formatter:on
        return homes;
    }
}
