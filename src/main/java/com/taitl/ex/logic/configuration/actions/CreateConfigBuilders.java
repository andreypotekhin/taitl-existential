package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.contexts.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Provides ConfigBuilder instances and builds resulting configs, outputting to ConfigRegistry.
 */
public class CreateConfigBuilders
{
    protected Map<String, ConfigBuilder> configBuilders;
    protected ConfigurationLogic cl;

    public CreateConfigBuilders(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.configBuilders = cl.configBuilders();
    }

    public ConfigBuilder createBuilder(String op)
    {
        sane(op, "op");
        ConfigBuilder o = new ConfigBuilder(op);
        synchronized (this)
        {
            for (Context context : cl.buildContexts(op))
            {
                o.addContext(context);
            }
            configBuilders.put(op, o);
        }
        return o;
    }

    public ConfigBuilder getcreateBuilder(String op)
    {
        sane(op, "op");
        ConfigBuilder o = configBuilders.get(op);
        return (o != null) ? o : createBuilder(op);
    }
}
