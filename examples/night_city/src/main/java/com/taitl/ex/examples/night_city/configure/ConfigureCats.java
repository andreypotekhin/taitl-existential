package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.indexes.*;

import java.util.*;

public class ConfigureCats
{
    public void configureBirth(String context)
    {
        // @formatter:off
        Ex.configure()
            .context(context)
                .invariant(Cat.class)
                    .create(cat -> "Black".equals(cat.color), "Cats are born black");
        // @formatter:on
    }

    public void configureLifecycle(String context, Map<Cat, ? extends Collection<Dwelling<Cat, ?>>> dwellings)
    {
        configureBirth(context);
        MultiJoin<Cat, Dwelling<Cat, ?>, String> catDwelling =
                new MultiJoin<>(Cat::location, Dwelling::location);
        dwellings.forEach((cat, available) -> {
            catDwelling.addLeft(cat);
            catDwelling.addAllRight(available);
        });
        // @formatter:off
        Ex.configure()
            .context(context)
                .invariant(Cat.class)
                    .all(cat -> cat.location != null, "Every reported cat has a location")
                    .exists(catDwelling.left(), "Every cat must have a dwelling at its location")
                    .transit((before, after) -> before.color.equals(after.color),
                            "A cat's color cannot change during relocation");
        // @formatter:on
    }
}
