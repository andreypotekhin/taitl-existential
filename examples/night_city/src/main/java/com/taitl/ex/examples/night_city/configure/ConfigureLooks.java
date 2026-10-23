package com.taitl.ex.examples.night_city.configure;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.data.*;
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

    public void configureSelectionWorkload(String context, CityWorkload workload, int rules, String shape,
            AtomicLong evaluations)
    {
        if (rules < 1 || !("sparse".equals(shape) || "broad".equals(shape) || "clustered".equals(shape)
                || "upper-bound".equals(shape)))
        {
            throw new IllegalArgumentException(
                    "Use positive rules and sparse, broad, clustered, or upper-bound selection shape");
        }
        for (int i = 0; i < rules; i++)
        {
            TypeKey<?> type = "upper-bound".equals(shape) ? new TypeKey<Look<? extends Being<?>>>() {
            } : "broad".equals(shape) ? new TypeKey<Look<?>>() {
            } : workload.looks.get(("clustered".equals(shape) ? i / 16 : i) % workload.descriptors).type;
            configureSelectionRule(context, type, evaluations);
        }
    }

    protected <T> void configureSelectionRule(String context, TypeKey<T> type, AtomicLong evaluations)
    {
        // @formatter:off
        Ex.configure().context(context).invariant(type)
            .create(entity -> {
                evaluations.incrementAndGet();
                return true;
            }, "Count selected Night City declarations");
        // @formatter:on
    }

}
