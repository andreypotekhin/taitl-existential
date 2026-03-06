package com.taitl.ex.examples.night_city.configure;

import com.taitl.existential.*;
import com.taitl.existential.configs.*;

public class ConfigureTransaction
{
    public void configureTransaction()
    {
        // @formatter:off
        Ex.configure()
            .context("/api/cats")
                .transaction("/api/cats/transaction")
                    .begin((Transaction tr) -> tr.index("cats", Object::toString).clear())
                    .commit((Transaction tr) -> tr.index("cats", Object::toString).clear())
                    .rollback((Transaction tr) -> tr.index("cats", Object::toString).clear())
                    .checkpoint((Transaction tr) -> tr.index("cats", Object::toString).clear());
        // @formatter:on
    }
}
