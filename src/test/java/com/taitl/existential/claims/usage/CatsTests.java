package com.taitl.existential.claims.usage;

import com.taitl.existential.Existential;
import com.taitl.existential.contexts.Context;
import com.taitl.existential.invariants.Effect;
import com.taitl.existential.invariants.Invariant;
import com.taitl.existential.keys.TypeKey;
import com.taitl.existential.model.cats.Cat;
import com.taitl.existential.model.cats.Location;
import com.taitl.existential.model.cats.TestData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CatsTests
{
    public Existential ex;
    public String op;

    public CatsTests(Existential ex, String op)
    {
        this.ex = ex;
        this.op = op;
    }

    public void configure()
    {
        ex.contexts.configure(op)
                .context(new Context(op) {
                    {
                        require(new Invariant<Cat>() {
                            {
                                create(c -> "Black".equals(c.color), "Cats are born black");
                            }
                        });
                        require(new Effect<Cat>() {
                            {
                                create(c -> c.location = new Location("Park"), "Set location for all new cats");
                            }
                        });
                    }
                });
    }

    public void configureWithBuilders()
    {
        ex.contexts.configure(op)
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
        ex.contexts.configure(op)
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