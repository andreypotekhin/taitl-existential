package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.invariants.*;

public class ConfigureApp
{
    public void configure()
    {
        // @formatter:off
        Ex.configure("/api/cats")
            .context("/api/cats/create")
                .invariant(Cat.class)
                    .create(c -> "Black".equals(c.color), "Cats are born black")
                    .done()
                .effect(Cat.class)
                    .create(c -> c.location = new Location("Park"), "Set location for all new cats")
                    .done()
                .build();
        // TODO:
        // ex.configure("/api/houses")
        // .context(new Context("/api/houses/create") {
        // { can't build house on North st.
        // .context(new Context("/api/houses/update") {
        // { can't move house where there is a Being
        // .context(new Context("/api/houses/delete") {
        // { can't delete house where exists a Being
        // @formatter:on
    }

    // public void configureWithClasses()
    // {
    // Ex.configure("/api/cats")
    // .context(new Context("/api/cats/create") {
    // {
    // invariant(Cat.class)
    // .create(c -> "Black".equals(c.color), "Cats are born black");
    // effect(Cat.class)
    // .create(c -> c.location = new Location("Park"), "Set location for all new cats");
    // }
    // });
    // }

    public void configureWithInstances()
    {
        Ex.configure("/api/cats")
                .context(new Context("/api/cats/create") {
                    {
                        invariant(new Invariant<Cat>() {
                            {
                                create(c -> "Black".equals(c.color), "Cats are born black");
                            }
                        });
                        effect(new Effect<Cat>() {
                            {
                                create(c -> c.location = new Location("Park"), "Set location for all new cats");
                            }
                        });
                    }
                });
    }

    public void configureMixingFluentAndBuilders()
    {
        Ex.configure("/api/cats")
                .context("/api/cats/create")
                .invariant(Cat.class)
                .create(c -> "Black".equals(c.color), "Cats are born black")
                .done()
                .effect(new Effect<Cat>() {
                    {
                        create(c -> c.location = new Location("Park"), "Set location for all new cats");
                    }
                })
                .done()
                .build();
    }
}