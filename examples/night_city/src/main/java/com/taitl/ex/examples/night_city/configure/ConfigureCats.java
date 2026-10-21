package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.quantifiers.*;

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
        Exists<Cat> hasDwelling = new Exists<>(dwellings,
                (cat, available) -> available.stream()
                        .anyMatch(dwelling -> cat.location().equals(dwelling.location())),
                "Every cat must have a dwelling at its location");
        // @formatter:off
        Ex.configure()
            .context(context)
                .invariant(Cat.class)
                    .create(hasDwelling, "Every new cat must have a dwelling at its location")
                .invariant(Cat.class)
                    .all(cat -> cat.location != null, "Every reported cat has a location")
                    .transit((before, after) -> before.color.equals(after.color) && hasDwelling.test(after),
                            "A cat's color cannot change during relocation");
        // @formatter:on
    }
}
