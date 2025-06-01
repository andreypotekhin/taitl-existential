package com.taitl.examples.night_city.commands.configure;

import com.taitl.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.invariants.*;

public class ConfigureApp
{
    public Existential ex;

    public ConfigureApp(Existential ex)
    {
        this.ex = ex;
    }

    public void configure()
    {
        ex.op("/api/cats")
                .context(new Context("/api/cats/create") {
                    {
                        invariant(Cat.class)
                                .create(c -> "Black".equals(c.color), "Cats are born black");
                        effect(Cat.class)
                                .create(c -> c.location = new Location("Park"), "Set location for all new cats");
                    }
                });
        // TODO:
        // ex.op("/api/houses")
        // .context(new Context("/api/houses/create") {
        // { can't build house on North st.
        // .context(new Context("/api/houses/update") {
        // { can't move house where there is a Being
        // .context(new Context("/api/houses/delete") {
        // { can't delete house where exists a Being
    }

    public void configureWithInnerClasses()
    {
        ex.op("/api/cats")
                .context(new Context("/api/cats/create") {
                    {
                        enforce(new Invariant<Cat>() {
                            {
                                create(c -> "Black".equals(c.color), "Cats are born black");
                            }
                        });
                        cause(new Effect<Cat>() {
                            {
                                create(c -> c.location = new Location("Park"), "Set location for all new cats");
                            }
                        });
                    }
                });
    }

    public void configureWithBuilders()
    {
        ex.op("/api/cats")
                .context("/api/cats/create")
                .invariant(Cat.class)
                .create(c -> "Black".equals(c.color), "Cats are born black")
                .done()
                .effect(Cat.class)
                .create(c -> c.location = new Location("Park"), "Set location for all new cats")
                .done()
                .build();
    }

    public void configureMixingFluentAndBuilders()
    {
        ex.op("/api/cats")
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