package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.Cat;
import com.taitl.ex.examples.night_city.model.Location;
import com.taitl.existential.Ex;
import com.taitl.existential.configs.Context;
import com.taitl.existential.configs.Transaction;
import com.taitl.existential.constraints.Effect;
import com.taitl.existential.constraints.Invariant;

public class ConfigureClassRules
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
        // @formatter:off
        Ex.configure("/api/cats")
            .context(new Context("/api/cats/create") {{
                invariant(new Invariant<Cat>() {{
                    create(c -> "Black".equals(c.color), "Cats are born black");
                }});
                effect(new Effect<Cat>() {{
                    create(c -> c.location = new Location("Park"), "Set location for all new cats");
                }});
            }});
        // @formatter:on
    }

    public void configureMixingFluentAndBuilders()
    {
        // @formatter:off
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
        // @formatter:on
    }

    public void configureTransactionRules()
    {
        // @formatter:off
        Ex.configure("/api/cats")
            .context("/api/cats")
                .transaction("/api/cats/transaction")
                    .begin((Transaction tr) -> tr.index("cats").clear())
                    .commit((Transaction tr) -> tr.index("cats").clear())
                    .rollback((Transaction tr) -> tr.index("cats").clear())
                    .checkpoint((Transaction tr) -> tr.index("cats").clear())
                .build()
            .build();
        // @formatter:on
    }
}
