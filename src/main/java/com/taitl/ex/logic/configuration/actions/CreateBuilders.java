package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;

import static com.taitl.ex.common.helper.Args.*;

/**
 * Provides ConfigBuilder instances and builds the Configs,
 * also adding to ConfigurationLogic.
 */
public class CreateBuilders
{
    protected Map<String, ConfigBuilder> configBuilders;
    protected ConfigurationLogic cl;

    public CreateBuilders(ConfigurationLogic cl)
    {
        this.cl = cl;
        this.configBuilders = cl.configBuilders();
    }

    public ConfigBuilder getCreateBuilder(String op)
    {
        sane(op, "op");
        ConfigBuilder o = configBuilders.get(op);
        return (o != null) ? o : createBuilder(op);
    }

    /**
     * @deprecated Use {@link #getCreateBuilder(String)} instead.
     */
    @Deprecated
    public ConfigBuilder getcreateBuilder(String op)
    {
        return getCreateBuilder(op);
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
}
