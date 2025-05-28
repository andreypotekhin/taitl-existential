package com.taitl.existential.examples.night_city.tests;

import com.taitl.existential.Existential;
import com.taitl.existential.contexts.Context;
import com.taitl.existential.effects.Effect;
import com.taitl.existential.invariants.Invariant;
import com.taitl.existential.examples.night_city.model.Cat;
import com.taitl.existential.examples.night_city.model.Location;

public class CityTests
{
    public Existential ex;
    public String op;

    public CityTests(Existential ex, String op)
    {
        this.ex = ex;
        this.op = op;
    }

    public void configure()
    {
        ex.configure(op)
                .context(new Context(op) {
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
        ex.configure(op)
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
        ex.configure(op)
                .context()
                .invariant(Cat.class)
                .create(c -> "Black".equals(c.color), "Cats are born black")
                .done()
                .require(new Effect<Cat>() {
                    {
                        create(c -> c.location = new Location("Park"), "Set location for all new cats");
                    }
                })
                .done()
                .build();
    }
}