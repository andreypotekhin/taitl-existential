package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.existential.*;
import com.taitl.existential.builders.*;
import com.taitl.existential.keys.*;

import java.util.concurrent.atomic.*;

public class ConfigureLooks
{
    public void configureWorkload(String context, int ruleCount, AtomicLong evaluations)
    {
        if (ruleCount < 1)
        {
            throw new IllegalArgumentException("Rule count must be positive");
        }
        for (int i = 0; i < ruleCount; i++)
        {
            int rule = i;
            // @formatter:off
            Ex.configure()
                .context(context)
                    .invariant(new TypeKey<Look<?>>() {})
                        .create(look -> {
                            evaluations.incrementAndGet();
                            return true;
                        }, "Night City look workload rule " + rule);
            // @formatter:on
        }
    }

    public void configureImmediateWorkload(String context, int ruleCount, AtomicLong evaluations)
    {
        if (ruleCount < 1)
        {
            throw new IllegalArgumentException("Workload rule count must be positive");
        }
        for (int i = 0; i < ruleCount; i++)
        {
            int rule = i;
            // @formatter:off
            ContextBuilder ctx = Ex.configure().context(context).immediate();
            ctx.effect(new TypeKey<Look<?>>() {})
                .create(look -> evaluations.incrementAndGet(), "Night City immediate look rule " + rule);
            // @formatter:on
        }
    }
}
