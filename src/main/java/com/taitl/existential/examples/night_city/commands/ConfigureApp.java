package com.taitl.existential.examples.night_city.commands;

import com.taitl.existential.*;
import com.taitl.existential.contexts.*;
import com.taitl.existential.effects.*;
import com.taitl.existential.examples.night_city.model.*;
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
                .context(new Context("/api/cats") {
                    {
                        invariant(Cat.class)
                                .create(c -> "Black".equals(c.color), "Cats are born black");
                        effect(Cat.class)
                                .create(c -> c.location = new Location("Park"), "Set location for all new cats");
                    }
                });
    }

    public void configureWithAnonymousInnerClasses()
    {
        ex.op("/api/cats")
                .context(new Context("/api/cats") {
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
                .context()
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
                .context()
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