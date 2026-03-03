package com.taitl.ex.logic.configuration.actions;

import java.util.*;
import com.taitl.ex.logic.configuration.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.keys.*;

import static com.taitl.ex.common.helper.Args.*;

public class BuildConfig
{
    protected ConfigurationLogic cl;

    public BuildConfig(ConfigurationLogic cl)
    {
        this.cl = cl;
    }

    /**
     * Builds Config from ConfigBuilder and adds it to ConfigRegistry.
     */
    public void call(ConfigBuilder configBuilder)
    {
        sane(configBuilder, "configBuilder");
        Config config = configBuilder.build(cl.ec());
        cl.registry().addConfig(config);

        Set<String> ops = new LinkedHashSet<>();
        for (Context context : config.contexts())
        {
            if (isWildcardContext(context))
            {
                continue;
            }
            ops.add(context.name());
        }
        for (String op : ops)
        {
            cl.onFinishConfiguration(op);
        }
    }

    protected boolean isWildcardContext(Context context)
    {
        sane(context, "context");
        return new ContextKey(context.name()).isWildcard();
    }
}
