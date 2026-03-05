package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.ex.examples.night_city.model.taxonomy.*;
import com.taitl.existential.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.examples.night_city.data.CityTestData.*;

public class ConfigureEntityRules
{
    public void defaultConfig()
    {
        // @formatter:off
        // Single-entity rules
        Ex.configure()
            .context("/")
                .invariant(House.class)
                    .create(h -> h.address != null, "Every house must have an address");

        // Rules involving generic classes
        Ex.configure()
            .context("/")
                .invariant(new TypeKey<Building<?>>(){})
                    .create(b -> !b.color.equals("Boring"), "No boring buildings")
                .invariant(new TypeKey<Building>(){})
                    .create(b -> !b.color.equals("Bleak"), "No bleak buildings")
                .invariant(new TypeKey<Building<Brick>>(){})
                    .create(b -> !b.color.equals("Miserable"), "No miserable houses")
                .invariant(new TypeKey<Building<Cardboard>>(){})
                    .create(b -> b.color.equals("Pink"),
                            "Cardboard buildings should be pink!")
                ;

        Ex.configure()
            .context("/")
                .invariant(new TypeKey<Being<?>>(){})
                    .create(b -> !b.color.equals("Invisible"), "No invisible begins")
                .invariant(new TypeKey<Being>(){})
                    .create(b -> !b.color.equals("Unattractive"), "No unattractive begins")
                .invariant(new TypeKey<Being<Felidae>>(){})
                    .create(b -> !b.color.equals("Blue"), "No blue cats")
                .invariant(new TypeKey<Being<Muridae>>(){})
                    .create(b -> !b.color.equals("Neon"), "No neon mice")
        ;

        // Existence rules
        // Single-entity existence
        Ex.configure()
                .context("/")
                .invariant(Mouse.class)
                    .exists(MICE, m -> m.color().equals("Red"));

        // Cross-entity existence - static map
        Ex.configure()
            .context("/")
                .invariant(Mouse.class)
                    .exists(MOUSE_DWELLING_MAP, "At the end of transaction, every mouse is in a dwelling")
        ;

        // Cross-entity existence - dynamic map (adjusts with entity changes)
        Ex.configure()
            .context("/")
                .effect(Mouse.class)
                    .transit((t0, t1) -> mouseDwellingJoin.reindexLeft(t0.location(), t1.location(), t1),
                            "Update mouse-dwelling on mouse movements")
                .effect(Dwelling.class)
                    .transit((t0, t1) -> mouseDwellingJoin.reindexRight(t0.location(), t1.location(), t1),
                           "Update mouse-dwelling on dwelling movements")
//                .invariant(Mouse.class)
//                    .exists(mouseDwellingJoin.left(), "At the end of transaction, every mouse is in a dwelling")
        ;

        // Rules for narrower contexts
//        Ex.configure()
//                .context("/api/cats")

        // @formatter:on
    }

    public void configure()
    {
        // @formatter:off
        Ex.configure()
            .context("/api/cats")
                .invariant(Cat.class)
                    .create(c -> "Black".equals(c.color), "Cats are born black")
                .effect(Cat.class)
                    .create(c -> c.location = new Location("Park"),
                            "Auto-set location for new cats");
        // @formatter:on

        // TODO:
        // ex.configure()
        // .context(new Context("/api/houses/create") {
        // { can't build house on North st.
        // .context(new Context("/api/houses/update") {
        // { can't move house where there is a Being
        // .context(new Context("/api/houses/delete") {
        // { can't delete house where exists a Being
        defaultConfig();
    }

    public void configureWithTypeKeys()
    {
        // @formatter:off
         Ex.configure()
             .context("/api/cats")
                 .invariant(new TypeKey<Cat>(){})
                     .create(c -> "Black".equals(c.color), "Cats are born black")
                 .effect(new TypeKey<Cat>(){})
                     .create(c -> c.location = new Location("Park"),
                             "Set default location for new cats");
         // @formatter:on
        defaultConfig();
    }

    public void configureWithInstances()
    {
        // @formatter:off
        Ex.configure()
            .context(new Context("/api/cats") {{
                invariant(new Invariant<Cat>() {{
                    create(c -> "Black".equals(c.color), "Cats are born black");
                }});
                effect(new Effect<Cat>() {{
                    create(c -> c.location = new Location("Park"), "Set location for all new cats");
                }});
            }});
        // @formatter:on
        defaultConfig();
    }

    public void configureMixingFluentAndBuilders()
    {
        // @formatter:off
        Ex.configure()
            .context("/api/cats")
            .invariant(Cat.class)
                .create(c -> "Black".equals(c.color), "Cats are born black")
            .effect(new Effect<Cat>() {
                {
                    create(c -> c.location = new Location("Park"), "Set location for all new cats");
                }
            });
        // @formatter:on
        defaultConfig();
    }
}
