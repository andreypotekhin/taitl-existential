package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.indexes.*;
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
        configureState(context);
        MultiJoin<Cat, Dwelling<Cat, ?>, String> catDwelling =
                new MultiJoin<>((cat) -> cat.location(), (dwelling) -> dwelling.location());
        dwellings.forEach((cat, available) -> {
            catDwelling.addLeft(cat);
            catDwelling.addAllRight(available);
        });
        // @formatter:off
        Ex.configure()
            .context(context)
                .invariant(Cat.class)
                    .exists(catDwelling.left(), "Every cat must have a dwelling at its location");
        // @formatter:on
    }

    public void configureMembership(String context, Collection<Cat> cats)
    {
        configureState(context);
        // @formatter:off
        Ex.configure().context(context)
                .invariant(Cat.class)
                    .all(new Exists<>(cats), "Every reported cat belongs to the supplied collection");
        // @formatter:on
    }

    public void configureRelationships(String context, Map<Cat, ? extends Dwelling<Cat, ?>> dwellings)
    {
        configureState(context);
        Exists<Cat> hasDwelling = new Exists<>(dwellings,
                (cat, dwelling) -> cat.location != null && dwelling != null
                        && Objects.equals(cat.location(), dwelling.location()));
        // @formatter:off
        Ex.configure().context(context)
                .invariant(Cat.class)
                    .all(hasDwelling, "Every cat must have a dwelling at its location");
        // @formatter:on
    }

    void configureState(String context)
    {
        configureBirth(context);
        // @formatter:off
        Ex.configure().context(context)
                .invariant(Cat.class)
                    .all(cat -> cat.location != null, "Every reported cat has a location")
                    .transit((before, after) -> Objects.equals(before.color, after.color),
                            "A cat's color cannot change during relocation");
        // @formatter:on
    }
}
